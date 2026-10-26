# DeepIndex AI — Technical Architecture Document

## 1. System Overview & Architecture Goals
DeepIndex AI is designed around a **Serverless, Edge-Cached, Zero-Operational-Cost** topology. It delivers sub-50ms feed load times on mobile devices while processing incoming technical research, enterprise case studies, legal policy updates, and open-weight models completely automatically.

### Key Metrics & SLAs
* **Operational Cost:** $0.00 / month (utilizing Cloudflare Workers, GitHub Actions, Supabase Free Tier, and Gemini API Free Tier).
* **App Feed Render Speed:** 0ms (Cache-first local Room DB rendering).
* **Cold Database Spin-up Impact:** 0ms for general feed browsing (only invoked during vector search queries).

---

## 2. End-to-End System Topology

```
┌────────────────────────────────────────────────────────────────────────┐
│                        DATA INGESTION LAYER                            │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────────────┐  │
│  │ arXiv API    │      │ RSS Feeds    │      │ GitHub Release APIs  │  │
│  └──────┬───────┘      └──────┬───────┘      └──────────┬───────────┘  │
└─────────┼─────────────────────┼─────────────────────────┼──────────────┘
          │                     │                         │
          └─────────────────────┼─────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   ASYNC INGESTION & PROCESSING ENGINE                  │
│                     (GitHub Actions Workflow Cron)                     │
│  1. Deduplicate via Hash Register                                      │
│  2. Send raw text to Gemini 2.5 Flash API (Structured Pydantic Model)  │
│  3. Generate 384-dim Dense Vectors via Hugging Face Inference API      │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      PERSISTENCE & STORAGE LAYER                       │
│  ┌─────────────────────────────────┐   ┌────────────────────────────┐  │
│  │ Cloudflare R2 / Vercel Edge CDN │   │ Supabase PostgreSQL DB     │  │
│  │ • feed_latest.json (Static)      │   │ • articles & metadata      │  │
│  │ • High speed edge distribution   │   │ • pgvector (HNSW Index)    │  │
│  └────────────────┬────────────────┘   └─────────────┬──────────────┘  │
└───────────────────┼──────────────────────────────────┼─────────────────┘
                    │                                  │
                    ▼ (HTTP Sync)                      ▼ (RPC Vector Query)
┌────────────────────────────────────────────────────────────────────────┐
│                       ANDROID CLIENT APPLICATION                       │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ Kotlin / Jetpack Compose UI (Big & Bold Micro-Learning Cards)    │  │
│  ├──────────────────────────────────────────────────────────────────┤  │
│  │ Room Database (Local SQLite Cache) + WorkManager Background Sync │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Data Flow & Execution Stages

### Stage 1: Ingestion & Deduplication
1. A GitHub Action executes every 3 hours via `cron`.
2. The `agent.py` fetcher queries raw APIs (arXiv, GitHub, engineering RSS).
3. URL hashes (`MD5(source_url)`) are checked against current records to discard existing items.

### Stage 2: Gemini LLM Processing
1. Raw markdown or abstracts are sent to `gemini-2.5-flash` with strict JSON schema definitions enforced by Pydantic.
2. The LLM extracts:
   * Executive TL;DR
   * 3 Key Takeaways
   * Runnable Code / Kernel Snippet (if present)
   * Benchmark metrics (e.g., `MMLU`, `TFLOPS`)
   * Categorization (`casestudies`, `security`, `infra`, `models`, `compliance`)
   * Technical Depth (`L1`, `L2`, `L3`)

### Stage 3: Vector Embeddings & Static Feed Export
1. Article title and TL;DR are passed to the Hugging Face API to compute 384-dim BGE dense vectors.
2. The worker outputs a static `feed_latest.json` artifact containing the top 50 recent stories and uploads it to Cloudflare R2 / static storage.
3. Database records and vectors are upserted to Supabase Postgres `pgvector`.

### Stage 4: Mobile Sync & Render
1. The Android application uses `WorkManager` to download `feed_latest.json` in the background.
2. Articles are written to local **Room DB (SQLite)**.
3. The UI renders instantly from Room DB using Jetpack Compose StateFlows.

---

## 4. Vector Search Architecture (Hybrid Search)

When a developer searches for concepts like *"optimizing KV cache inference speed"*:

1. **Client Request:** The query string is sent to a Cloudflare Worker edge function.
2. **Embedding Generation:** The edge worker generates a vector for the query using the BGE Small embedding model.
3. **Supabase RPC Execution:** The worker invokes `search_articles()` in Postgres:
   $$\text{Similarity} = 1 - \text{CosineDistance}(\vec{E}_{\text{query}}, \vec{E}_{\text{article}})$$
4. **HNSW Filtered Retrieval:** The index filters results by category (`infra`, `models`) and returns the top $K=20$ matching records.

---

## 5. Security, Scalability, & Cost Analysis

| Component | Provider / Strategy | Quota / Free Tier Bound | Estimated Cost |
| :--- | :--- | :--- | :--- |
| **Ingestion Compute** | GitHub Actions | 2,000 mins/mo (Consumes ~180 mins) | **$0.00** |
| **LLM Inference** | Gemini 2.5 Flash API | 15 RPM / 1M TPM (Consumes ~50 req/day) | **$0.00** |
| **Embeddings API** | Hugging Face Free API | Unlimited rate-limited requests | **$0.00** |
| **Database & Vectors** | Supabase Postgres | 500 MB Storage / 50k MAU | **$0.00** |
| **CDN & Edge Feed** | Cloudflare R2 & Workers | 10 GB Storage / 10M Reads/mo | **$0.00** |
| **Total Monthly Spend** | — | — | **$0.00 / month** |