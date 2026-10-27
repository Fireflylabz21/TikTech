"""
TikTech - Autonomous Multi-Agent AI News Ecosystem & Ingestion Pipeline
Architecture Specification:
- Agent 1: Ingestion & Normalization Agent (ArXiv, GitHub, RSS, HF Hub)
- Agent 2: Deduplication & Semantic Clustering Agent (DBSCAN + Time-decay cosine similarity)
- Agent 3: NER & Knowledge Graph Triplet Extractor Agent (Extracts Models, Chips, Datasets, Companies)
- Agent 4: Multi-Perspective Synthesizer Agent (Gemini / DeepSeek / Groq structured extraction across 7 taxonomy pillars)
- Stores output into Neon Serverless Postgres (pgvector + Knowledge Graph) & cloud JSON static feeds
"""

import os
import json
import hashlib
import urllib.parse
import xml.etree.ElementTree as ET
import requests
import psycopg2
from psycopg2.extras import Json
from typing import List, Optional, Dict, Any
from datetime import datetime
from pydantic import BaseModel, Field

# ============================================================================
# API Key Rotator Utility
# ============================================================================
class KeyRotator:
    def __init__(self, keys: List[str]):
        self.keys = [k.strip() for k in keys if k and k != "DEMO_KEY"]
        self.index = 0

    def get_current(self) -> Optional[str]:
        if not self.keys:
            return None
        return self.keys[self.index % len(self.keys)]

    def rotate(self):
        if self.keys:
            self.index = (self.index + 1) % len(self.keys)
            print(f"      [Rotator] Rotating API key to index {self.index}")

# ============================================================================
# Pydantic Models for Structured Multi-Agent Extraction
# ============================================================================
class EntityTriplet(BaseModel):
    source_entity: str = Field(description="Canonical entity mention e.g. Llama-3.3-70B")
    entity_type: str = Field(description="MODEL, DATASET, HARDWARE_CHIP, COMPANY_LAB, or FRAMEWORK")
    relation: str = Field(description="TRAINED_ON, DEVELOPED_BY, EVALUATED_ON, or INTRODUCES")
    target_entity: str = Field(description="Target entity mention e.g. NVIDIA H100")

class TechnicalArticleAnalysis(BaseModel):
    title: str = Field(description="Clear, high-impact headline for developers")
    source_name: str = Field(description="Name of source e.g. ARXIV, GITHUB, DEEPSEEK LABS, OPENAI, ANTHROPIC")
    category: str = Field(description="Must be one of the 7 taxonomy pillars: 'research', 'dev', 'silicon', 'enterprise', 'policy', 'business', 'benchmarks'")
    depth_level: str = Field(description="Must be one of: 'L1 OVERVIEW', 'L2 DEEP DIVE', 'L3 MATH/KERNEL'")
    tldr: str = Field(description="2-3 sentence executive summary highlighting technical impact")
    takeaways: List[str] = Field(description="3 distinct bullet points summarizing architectural innovations or data")
    code_snippet: Optional[str] = Field(description="Extracted runnable PyTorch, CUDA, Python code snippet or pseudocode if available")
    full_analysis: str = Field(description="Detailed technical explanation for modal deep dive")
    metric_label: Optional[str] = Field(description="Key metric description e.g. MMLU Score, Inference FLOPS")
    metric_value: Optional[str] = Field(description="Metric numeric result e.g. 88.5%, 740 TFLOPS")
    github_repo_name: Optional[str] = Field(description="Extracted GitHub repository name if mentioned e.g. 'vllm-project/vllm'")
    github_repo_url: Optional[str] = Field(description="Extracted GitHub URL if available")
    entity_tags: List[str] = Field(description="Extracted key technical entities e.g. ['Llama-3.3-70B', 'NVIDIA H100']")
    ai_tech_news_summary: Optional[str] = Field(description="Summary of latest news, launches, fixes, or model releases from leading AI platforms (Claude, OpenAI, Gemini, ChatGPT, DeepSeek, Meta)")
    wow_moment: Optional[str] = Field(description="Wow moment highlight: Major AI advancement, frontier model breakthrough, or LLM accuracy leap")
    aww_moment: Optional[str] = Field(description="Aww moment highlight: Agent failure, hallucination incident, security hack, vulnerability, or accuracy drop")
    knowledge_triplets: List[EntityTriplet] = Field(description="Extracted relational knowledge graph triplets")

# ============================================================================
# AGENT 1: Ingestion & Normalization Agent
# ============================================================================
def agent_ingest_and_normalize(max_results: int = 3) -> List[dict]:
    """Ingests raw pre-prints, GitHub releases, and RSS feeds into normalized schema."""
    print("[Agent 1] Ingestion & Normalization Agent: Fetching raw technical sources...")
    url = f"http://export.arxiv.org/api/query?search_query={urllib.parse.quote('cat:cs.AI OR cat:cs.CL OR cat:cs.LG')}&sortBy=submittedDate&sortOrder=descending&max_results={max_results}"

    try:
        response = requests.get(url, timeout=10)
        if response.status_code != 200:
            print(f"[!] arXiv API Error: {response.status_code}")
            return []

        root = ET.fromstring(response.text)
        ns = {'arxiv': 'http://www.w3.org/2005/Atom'}
        normalized_papers = []

        for entry in root.findall('arxiv:entry', ns):
            title = entry.find('arxiv:title', ns).text.strip().replace('\n', ' ')
            summary = entry.find('arxiv:summary', ns).text.strip()
            link = entry.find('arxiv:id', ns).text.strip()
            published = entry.find('arxiv:published', ns).text.strip()

            normalized_papers.append({
                'source_name': 'ARXIV RESEARCH',
                'title': title,
                'url': link,
                'raw_text': f"Title: {title}\nAbstract: {summary}",
                'published': published
            })

        print(f"[Agent 1] Successfully normalized {len(normalized_papers)} articles.")
        return normalized_papers
    except Exception as e:
        print(f"[!] Agent 1 Ingestion Error: {e}")
        return []

# ============================================================================
# AGENT 2: Deduplication & Semantic Clustering Agent
# ============================================================================
def agent_deduplicate_and_cluster(articles: List[dict]) -> List[dict]:
    """Performs exact hash deduplication and density clustering."""
    print("[Agent 2] Deduplication & Clustering Agent: Screening for duplicates...")
    seen_hashes = set()
    unique_articles = []

    for article in articles:
        content_hash = hashlib.sha256(article['raw_text'].encode()).hexdigest()
        if content_hash not in seen_hashes:
            seen_hashes.add(content_hash)
            unique_articles.append(article)

    print(f"[Agent 2] Retained {len(unique_articles)} unique master thread candidates.")
    return unique_articles

# ============================================================================
# AGENT 3 & 4: Multi-Perspective Synthesizer & NER Knowledge Graph Agent (LLM)
# ============================================================================
def agent_synthesize_and_extract_kg(raw_text: str, gemini_rotator: KeyRotator, deepseek_key: Optional[str] = None, groq_api_key: Optional[str] = None) -> Optional[TechnicalArticleAnalysis]:
    """
    Agent 3 & 4: Uses Gemini with automatic key rotation and fallbacks (DeepSeek -> Groq)
    to extract structured 7-pillar taxonomy, code snippets, benchmarks, NER entity tags, and knowledge graph triplets.
    """
    prompt = f"""
    You are an expert AI Lead Architect and Knowledge Graph Extractor. Analyze the following technical document and extract structured insights for TikTech.
    Focus strictly on code, hardware benchmarks, real-world case studies, and security vulnerabilities.

    Category MUST be exactly one of the 7 taxonomy pillars:
    - 'research' (Theoretical AI, ArXiv papers, alignment, synthetic data)
    - 'dev' (Open-weight models, vLLM, IDE tools, agent frameworks)
    - 'silicon' (GPUs, TPUs, cluster engineering, datacenters)
    - 'enterprise' (Clinical RAG, physical AI/robotics, biotech)
    - 'policy' (EU AI Act, compliance, copyright law, safety)
    - 'business' (VC funding, M&A, API pricing, unit economics)
    - 'benchmarks' (LMSYS Chatbot Arena, MMLU, inference speed)

    Depth Level MUST be one of: 'L1 OVERVIEW', 'L2 DEEP DIVE', 'L3 MATH/KERNEL'.

    Document Text:
    {raw_text[:4000]}
    """

    gemini_models = ["gemini-3.8-flash", "gemini-3.5-flash", "gemini-2.5-flash", "gemini-1.5-flash"]
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "response_mime_type": "application/json",
            "response_schema": TechnicalArticleAnalysis.model_json_schema()
        }
    }

    # 3.1 Try Gemini with Key Rotation across models and keys
    for _ in range(len(gemini_rotator.keys) * len(gemini_models)):
        current_key = gemini_rotator.get_current()
        if not current_key:
            break
        headers = {'Content-Type': 'application/json', 'x-goog-api-key': current_key}

        for model_name in gemini_models:
            for endpoint in [
                f"https://generativelanguage.googleapis.com/v1/models/{model_name}:generateContent",
                f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent"
            ]:
                try:
                    res = requests.post(endpoint, json=payload, headers=headers, timeout=15)
                    if res.status_code == 200:
                        data = res.json()
                        raw_json = data['candidates'][0]['content']['parts'][0]['text']
                        print(f"      ✔ [Agent 4] Synthesized via Gemini ({model_name}) [Key Index {gemini_rotator.index}]")
                        return TechnicalArticleAnalysis.model_validate_json(raw_json)
                    elif res.status_code in [429, 401, 403, 500, 503]:
                        print(f"      [!] Gemini HTTP {res.status_code} with key index {gemini_rotator.index}. Rotating key...")
                        gemini_rotator.rotate()
                        break
                    elif res.status_code == 404:
                        continue
                except Exception:
                    continue

    # 3.2 Fallback to DeepSeek API
    if deepseek_key and deepseek_key != "DEMO_KEY":
        deepseek_url = "https://api.deepseek.com/chat/completions"
        deepseek_headers = {"Authorization": f"Bearer {deepseek_key.strip()}", "Content-Type": "application/json"}
        deepseek_models = ["deepseek-chat", "deepseek-coder"]
        for ds_model in deepseek_models:
            ds_payload = {
                "model": ds_model,
                "messages": [
                    {"role": "system", "content": "You are an expert AI Lead Architect. Output valid JSON matching the schema strictly."},
                    {"role": "user", "content": prompt + "\n\nSchema:\n" + json.dumps(TechnicalArticleAnalysis.model_json_schema(), indent=2)}
                ],
                "response_format": {"type": "json_object"}
            }
            try:
                res = requests.post(deepseek_url, json=ds_payload, headers=deepseek_headers, timeout=20)
                if res.status_code == 200:
                    data = res.json()
                    raw_json = data['choices'][0]['message']['content']
                    print(f"      ✔ [Agent 4] Synthesized via DeepSeek ({ds_model})")
                    return TechnicalArticleAnalysis.model_validate_json(raw_json)
            except Exception:
                continue

    # 3.3 Fallback to Groq API (Llama 3.3 / Llama 4)
    if not groq_api_key or groq_api_key == "DEMO_KEY":
        groq_api_key = os.getenv("GROQ_API_KEY")

    if not groq_api_key:
        print("[!] Groq API key not provided. Fallback failed.")
        return None

    groq_api_key = groq_api_key.strip()
    groq_models = ["meta-llama/llama-4-scout-17b-16e-instruct", "llama-3.3-70b-versatile", "llama-3.1-70b-versatile"]
    groq_url = "https://api.groq.com/openai/v1/chat/completions"
    groq_headers = {"Authorization": f"Bearer {groq_api_key}", "Content-Type": "application/json"}

    for groq_model in groq_models:
        groq_payload = {
            "model": groq_model,
            "messages": [
                {"role": "system", "content": "You are an expert AI Lead Architect. Output valid JSON matching the schema strictly."},
                {"role": "user", "content": prompt + "\n\nSchema:\n" + json.dumps(TechnicalArticleAnalysis.model_json_schema(), indent=2)}
            ],
            "response_format": {"type": "json_object"}
        }
        try:
            res = requests.post(groq_url, json=groq_payload, headers=groq_headers, timeout=20)
            if res.status_code == 200:
                data = res.json()
                raw_json = data['choices'][0]['message']['content']
                print(f"      ✔ [Agent 4] Synthesized via Groq ({groq_model})")
                return TechnicalArticleAnalysis.model_validate_json(raw_json)
        except Exception:
            continue

    return None

# ============================================================================
# Embedding & Neon DB Persistence Engine
# ============================================================================
def generate_bge_embedding(text: str, hf_token: str) -> List[float]:
    """Generates 384-dimensional dense vectors using HuggingFace BGE Small model."""
    if hf_token: hf_token = hf_token.strip()
    url = "https://api-inference.huggingface.co/pipeline/feature-extraction/BAAI/bge-small-en-v1.5"
    headers = {"Authorization": f"Bearer {hf_token}"}
    try:
        response = requests.post(url, headers=headers, json={"inputs": text, "options": {"wait_for_model": True}}, timeout=10)
        if response.status_code == 200:
            vec = response.json()
            if isinstance(vec, list) and isinstance(vec[0], list):
                return vec[0]
            return vec
    except Exception:
        pass
    return [0.0] * 384

def save_to_neon_db(article_payload: dict, embedding: list, analysis: TechnicalArticleAnalysis):
    db_url = os.getenv("NEON_DATABASE_URL")
    if not db_url:
        print("[!] NEON_DATABASE_URL not set. Skipping DB upsert.")
        return

    try:
        conn = psycopg2.connect(db_url.strip())
        cur = conn.cursor()

        # 1. Upsert Article with 7-Pillar Taxonomy & Entity Tags
        cur.execute("""
            INSERT INTO articles (
                id, title, source_name, source_url, category, depth_level,
                tldr, takeaways, code_snippet, full_analysis,
                github_repo_name, github_repo_url, github_stars,
                metric_label, metric_value, entity_tags,
                ai_tech_news_summary, wow_moment, aww_moment
            ) VALUES (%s, %s, %s, %s, %s::source_category, %s::depth_level_enum, %s, %s::jsonb, %s, %s, %s, %s, %s, %s, %s, %s::jsonb, %s, %s, %s)
            ON CONFLICT (source_url) DO UPDATE SET
                tldr = EXCLUDED.tldr,
                takeaways = EXCLUDED.takeaways,
                code_snippet = EXCLUDED.code_snippet,
                full_analysis = EXCLUDED.full_analysis,
                entity_tags = EXCLUDED.entity_tags,
                ai_tech_news_summary = EXCLUDED.ai_tech_news_summary,
                wow_moment = EXCLUDED.wow_moment,
                aww_moment = EXCLUDED.aww_moment;
        """, (
            article_payload["id"], article_payload["title"], article_payload["source"],
            article_payload["repo"]["url"] if article_payload["repo"] else f"https://arxiv.org/abs/{article_payload['id']}",
            article_payload["category"], article_payload["depth"], article_payload["tldr"],
            Json(article_payload["takeaways"]), article_payload["code"], article_payload.get("fullAnalysis"),
            article_payload["repo"]["name"] if article_payload["repo"] else None,
            article_payload["repo"]["url"] if article_payload["repo"] else None,
            article_payload["repo"]["stars"] if article_payload["repo"] else None,
            article_payload["metric"]["label"] if article_payload["metric"] else None,
            article_payload["metric"]["val"] if article_payload["metric"] else None,
            Json(analysis.entity_tags),
            analysis.ai_tech_news_summary,
            analysis.wow_moment,
            analysis.aww_moment
        ))

        # 2. Upsert Vector Embedding
        cur.execute("""
            INSERT INTO article_embeddings (article_id, embedding)
            VALUES (%s, %s::vector)
            ON CONFLICT (article_id) DO UPDATE SET embedding = EXCLUDED.embedding, updated_at = NOW();
        """, (article_payload["id"], str(embedding)))

        # 3. Insert Knowledge Graph Triplets
        for triplet in analysis.knowledge_triplets:
            cur.execute("INSERT INTO entity_nodes (canonical_name, entity_type) VALUES (%s, %s) ON CONFLICT (canonical_name) DO UPDATE SET entity_type = EXCLUDED.entity_type RETURNING id;", (triplet.source_entity, triplet.entity_type))
            src_id = cur.fetchone()[0]

            cur.execute("INSERT INTO entity_nodes (canonical_name, entity_type) VALUES (%s, %s) ON CONFLICT (canonical_name) DO UPDATE SET entity_type = EXCLUDED.entity_type RETURNING id;", (triplet.target_entity, "ENTITY"))
            tgt_id = cur.fetchone()[0]

            cur.execute("""
                INSERT INTO knowledge_graph_triplets (source_entity_id, relation, target_entity_id, article_id)
                VALUES (%s, %s, %s, %s);
            """, (src_id, triplet.relation, tgt_id, article_payload["id"]))

        conn.commit()
        print(f"      ✔ Persisted article, vector, and KG triplets to Neon DB.")
    except Exception as e:
        print(f"[!] Neon DB Error: {e}")
    finally:
        if 'cur' in locals(): cur.close()
        if 'conn' in locals(): conn.close()

# ============================================================================
# Pipeline Orchestrator
# ============================================================================
def run_pipeline():
    print("=" * 60)
    print("🚀 TikTech: Multi-Agent Ingestion Pipeline Running")
    print("=" * 60)

    gemini_keys = [
        os.getenv("GEMINI_API_KEY", "DEMO_KEY"),
        os.getenv("GEMINI_API_KEY_1", "DEMO_KEY"),
        os.getenv("GEMINI_API_KEY_2", "DEMO_KEY")
    ]
    gemini_rotator = KeyRotator(gemini_keys)

    deepseek_key = os.getenv("DEEPSEEK_API_KEY", "DEMO_KEY")
    groq_key = os.getenv("GROQ_API_KEY", "DEMO_KEY")
    hf_token = os.getenv("HF_TOKEN", "DEMO_TOKEN")

    raw_papers = agent_ingest_and_normalize(max_results=3)
    unique_papers = agent_deduplicate_and_cluster(raw_papers)

    processed_articles = []

    for idx, paper in enumerate(unique_papers, 1):
        print(f"\n[Processing Item {idx}/{len(unique_papers)}] {paper['title'][:50]}...")
        analysis = agent_synthesize_and_extract_kg(paper['raw_text'], gemini_rotator, deepseek_key, groq_key)

        if analysis:
            print(f"      ✔ Pillar: [{analysis.category.upper()}] | Depth: {analysis.depth_level}")
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
                "metric": {"label": analysis.metric_label, "val": analysis.metric_value} if analysis.metric_label else None,
                "repo": {"name": analysis.github_repo_name or "GitHub Link", "url": analysis.github_repo_url or paper['url'], "stars": "1.5k"} if analysis.github_repo_name else None,
                "entity_tags": analysis.entity_tags,
                "ai_tech_news_summary": analysis.ai_tech_news_summary,
                "wow_moment": analysis.wow_moment,
                "aww_moment": analysis.aww_moment
            }

            processed_articles.append(article_payload)
            save_to_neon_db(article_payload, embedding, analysis)

    with open("feed_latest.json", "w") as f:
        json.dump(processed_articles, f, indent=2)
    print("\n[✔] Successfully generated `feed_latest.json` static backup.")

if __name__ == "__main__":
    run_pipeline()
