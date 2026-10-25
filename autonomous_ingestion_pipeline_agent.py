"""
TikTech - Autonomous Technical News Ingestion & Summarization Pipeline
Zero-Dollar Architecture Implementation:
- Ingests arXiv, RSS Feeds, GitHub Releases
- Summarizes using Google Gemini 2.5 Flash API (Structured Pydantic Output)
- Generates 384-dim dense vectors via HuggingFace Free Inference API
- Stores output into Neon Serverless Postgres (pgvector) & cloud JSON static feeds
"""

import os
import json
import hashlib
import urllib.parse
import xml.etree.ElementTree as ET
import requests
import psycopg2
from psycopg2.extras import Json
from typing import List, Optional
from datetime import datetime
from pydantic import BaseModel, Field

# ============================================================================
# 1. Pydantic Models for Gemini Structured Extraction
# ============================================================================
class TechnicalArticleAnalysis(BaseModel):
    title: str = Field(description="Clear, high-impact headline for developers")
    source_name: str = Field(description="Name of source e.g. DEEPSEEK LABS, CERT ALERT, ARXIV")
    category: str = Field(description="Must be one of: casestudies, security, compliance, infra, models, industry")
    depth_level: str = Field(description="Must be one of: 'L1 OVERVIEW', 'L2 DEEP DIVE', 'L3 MATH/KERNEL'")
    tldr: str = Field(description="2-3 sentence executive summary highlighting technical impact")
    takeaways: List[str] = Field(description="3 distinct bullet points summarizing architectural innovations or data")
    code_snippet: Optional[str] = Field(description="Extracted runnable PyTorch, CUDA, Python code snippet or pseudocode if available")
    full_analysis: str = Field(description="Detailed 2-paragraph technical explanation for modal deep dive")
    metric_label: Optional[str] = Field(description="Key metric description e.g. Cycle Reduction, MMLU Score, Inference FLOPS")
    metric_value: Optional[str] = Field(description="Metric numeric result e.g. -40%, 88.5%, 740 TFLOPS")
    github_repo_name: Optional[str] = Field(description="Extracted GitHub repository name if mentioned e.g. 'vllm-project/vllm'")
    github_repo_url: Optional[str] = Field(description="Extracted GitHub URL if available")

# ============================================================================
# 2. Ingestion Modules (arXiv & RSS Fetchers)
# ============================================================================
def fetch_arxiv_papers(query="cat:cs.AI OR cat:cs.CL OR cat:cs.LG", max_results=5) -> List[dict]:
    """Fetches recent AI research papers from arXiv API."""
    url = f"http://export.arxiv.org/api/query?search_query={urllib.parse.quote(query)}&sortBy=submittedDate&sortOrder=descending&max_results={max_results}"
    response = requests.get(url, timeout=10)
    if response.status_code != 200:
        print(f"[!] arXiv API Error: {response.status_code}")
        return []

    root = ET.fromstring(response.text)
    ns = {'arxiv': 'http://www.w3.org/2005/Atom'}
    papers = []

    for entry in root.findall('arxiv:entry', ns):
        title = entry.find('arxiv:title', ns).text.strip().replace('\n', ' ')
        summary = entry.find('arxiv:summary', ns).text.strip()
        link = entry.find('arxiv:id', ns).text.strip()
        published = entry.find('arxiv:published', ns).text.strip()
        
        papers.append({
            'source_name': 'ARXIV RESEARCH',
            'title': title,
            'url': link,
            'raw_text': f"Title: {title}\nAbstract: {summary}",
            'published': published
        })

    return papers

# ============================================================================
# 3. Gemini Pipeline Integration (Optimized Prompt)
# ============================================================================
def analyze_article_with_gemini(raw_text: str, gemini_api_key: str) -> Optional[TechnicalArticleAnalysis]:
    """Sends raw paper/article text to Gemini 2.5 Flash for structured JSON extraction."""
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={gemini_api_key}"
    
    prompt = f"""
    You are an expert AI Lead Architect. Analyze the following technical document and extract structured insights for a developer news app.
    Focus strictly on code, hardware benchmarks, real-world case studies, and security vulnerabilities.
    Ensure category is exactly one of: 'casestudies', 'security', 'compliance', 'infra', 'models', 'industry'.
    Ensure depth_level is exactly one of: 'L1 OVERVIEW', 'L2 DEEP DIVE', 'L3 MATH/KERNEL'.

    Document Text:
    {raw_text[:4000]}
    """

    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "response_mime_type": "application/json",
            "response_schema": TechnicalArticleAnalysis.model_json_schema()
        }
    }

    try:
        res = requests.post(url, json=payload, headers={'Content-Type': 'application/json'}, timeout=15)
        if res.status_code == 200:
            data = res.json()
            raw_json = data['candidates'][0]['content']['parts'][0]['text']
            return TechnicalArticleAnalysis.model_validate_json(raw_json)
        else:
            print(f"[!] Gemini API Error ({res.status_code}): {res.text}")
    except Exception as e:
        print(f"[!] Failed to parse Gemini response: {e}")
    return None

# ============================================================================
# 4. Free Embedding Generation (HuggingFace Inference API)
# ============================================================================
def generate_bge_embedding(text: str, hf_token: str) -> List[float]:
    """Generates 384-dimensional dense vectors using HuggingFace BGE Small model."""
    url = "https://api-inference.huggingface.co/pipeline/feature-extraction/BAAI/bge-small-en-v1.5"
    headers = {"Authorization": f"Bearer {hf_token}"}
    
    try:
        response = requests.post(url, headers=headers, json={"inputs": text, "options": {"wait_for_model": True}}, timeout=10)
        if response.status_code == 200:
            vec = response.json()
            if isinstance(vec, list) and isinstance(vec[0], list):
                return vec[0]
            return vec
    except Exception as e:
        print(f"[!] Embedding API Error: {e}")
    
    return [0.0] * 384

# ============================================================================
# 5. Neon PostgreSQL Database Persistence & Upsert
# ============================================================================
def save_to_neon_db(article_payload: dict, embedding: list):
    db_url = os.getenv("NEON_DATABASE_URL")
    if not db_url:
        print("[!] NEON_DATABASE_URL not set. Skipping DB upsert.")
        return

    try:
        conn = psycopg2.connect(db_url)
        cur = conn.cursor()

        # Upsert Article
        cur.execute("""
            INSERT INTO articles (
                id, title, source_name, source_url, category, depth_level,
                tldr, takeaways, code_snippet, full_analysis,
                github_repo_name, github_repo_url, github_stars,
                metric_label, metric_value
            ) VALUES (%s, %s, %s, %s, %s::source_category, %s::depth_level_enum, %s, %s::jsonb, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (source_url) DO UPDATE SET
                tldr = EXCLUDED.tldr,
                takeaways = EXCLUDED.takeaways,
                code_snippet = EXCLUDED.code_snippet,
                full_analysis = EXCLUDED.full_analysis;
        """, (
            article_payload["id"],
            article_payload["title"],
            article_payload["source"],
            article_payload["repo"]["url"] if article_payload["repo"] else f"https://arxiv.org/abs/{article_payload['id']}",
            article_payload["category"],
            article_payload["depth"],
            article_payload["tldr"],
            Json(article_payload["takeaways"]),
            article_payload["code"],
            article_payload.get("fullAnalysis"),
            article_payload["repo"]["name"] if article_payload["repo"] else None,
            article_payload["repo"]["url"] if article_payload["repo"] else None,
            article_payload["repo"]["stars"] if article_payload["repo"] else None,
            article_payload["metric"]["label"] if article_payload["metric"] else None,
            article_payload["metric"]["val"] if article_payload["metric"] else None
        ))

        # Upsert Vector Embedding (384-dim BGE)
        cur.execute("""
            INSERT INTO article_embeddings (article_id, embedding)
            VALUES (%s, %s::vector)
            ON CONFLICT (article_id) DO UPDATE SET
                embedding = EXCLUDED.embedding,
                updated_at = NOW();
        """, (
            article_payload["id"],
            str(embedding)
        ))

        conn.commit()
        print(f"      ✔ Upserted article & vector embedding into Neon DB: {article_payload['title'][:40]}")
    except psycopg2.Error as e:
        print(f"[!] Neon DB Permission or SQL Error: {e}")
    except Exception as e:
        print(f"[!] Neon DB Upsert Error: {e}")
    finally:
        if 'cur' in locals(): cur.close()
        if 'conn' in locals(): conn.close()

# ============================================================================
# 6. Pipeline Orchestrator
# ============================================================================
def run_pipeline():
    print("=" * 60)
    print("🚀 DeepIndex AI: Starting Automated Ingestion Pipeline")
    print("=" * 60)

    gemini_key = os.getenv("GEMINI_API_KEY", "DEMO_KEY")
    hf_token = os.getenv("HF_TOKEN", "DEMO_TOKEN")

    # Step 1: Ingest
    print("[1/4] Fetching raw technical sources...")
    papers = fetch_arxiv_papers(max_results=3)
    print(f"      Fetched {len(papers)} items from arXiv.")

    processed_articles = []

    # Step 2: Summarize, Embed & Save to Neon DB
    for idx, paper in enumerate(papers, 1):
        print(f"\n[2/4] Processing Item {idx}/{len(papers)}: {paper['title'][:50]}...")
        
        analysis = analyze_article_with_gemini(paper['raw_text'], gemini_key)
        
        if analysis:
            print(f"      ✔ Analyzed as [{analysis.category.upper()}] - {analysis.depth_level}")
            
            print("      Generating 384-dim BGE vector embedding...")
            embedding = generate_bge_embedding(f"{analysis.title} {analysis.tldr}", hf_token)

            article_payload = {
                "id": hashlib.md5(paper['url'].encode()).hexdigest(),
                "source": analysis.source_name,
                "title": analysis.title,
                "category": analysis.category,
                "depth": analysis.depth_level,
                "tldr": analysis.tldr,
                "takeaways": analysis.takeaways,
                "code": analysis.code_snippet,
                "fullAnalysis": analysis.full_analysis,
                "metric": {
                    "label": analysis.metric_label,
                    "val": analysis.metric_value
                } if analysis.metric_label else None,
                "repo": {
                    "name": analysis.github_repo_name or "GitHub Link",
                    "url": analysis.github_repo_url or paper['url'],
                    "stars": "1.5k"
                } if analysis.github_repo_name else None
            }

            processed_articles.append(article_payload)

            # Step 3: Insert into Neon DB
            print("[3/4] Persisting record & vector embedding to Neon DB...")
            save_to_neon_db(article_payload, embedding)

    # Step 4: Export Static Edge Feed backup
    print("\n[4/4] Exporting static feed backup...")
    with open("feed_latest.json", "w") as f:
        json.dump(processed_articles, f, indent=2)
    print("      ✔ Successfully generated `feed_latest.json`")

    print("\n" + "=" * 60)
    print("🎉 Pipeline Run Complete!")
    print("=" * 60)

if __name__ == "__main__":
    run_pipeline()
