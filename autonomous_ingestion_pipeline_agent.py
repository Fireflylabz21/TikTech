import os
import psycopg2
from psycopg2.extras import Json

def save_to_neon_db(article_payload: dict, embedding: list):
    db_url = os.getenv("NEON_DATABASE_URL")
    if not db_url:
        print("[!] NEON_DATABASE_URL not set. Skipping DB upsert.")
        return

    conn = psycopg2.connect(db_url)
    cur = conn.cursor()
    try:
        # 1. Upsert Article
        cur.execute("""
            INSERT INTO articles (
                id, title, source_name, source_url, category, depth_level,
                tldr, takeaways, code_snippet, full_analysis,
                github_repo_name, github_repo_url, github_stars,
                metric_label, metric_value
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
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

        # 2. Upsert Vector Embedding (384-dim BGE)
        cur.execute("""
            INSERT INTO article_embeddings (article_id, embedding)
            VALUES (%s, %s::vector)
            ON CONFLICT (article_id) DO UPDATE SET
                embedding = EXCLUDED.embedding,
                updated_at = NOW();
        """, (
            article_payload["id"],
            str(embedding) # pgvector accepts list string representation e.g. "[0.1, -0.2, ...]"
        ))

        conn.commit()
        print(f"      ✔ Upserted article & embedding into Neon DB: {article_payload['title'][:40]}")
    except Exception as e:
        conn.rollback()
        print(f"[!] Neon DB Upsert Error: {e}")
    finally:
        cur.close()
        conn.close()
