# DeepIndex AI — Product Requirements Document (PRD) & UI Philosophy

## 1. Executive Summary & Core Value Proposition
**DeepIndex AI** is a mobile-first, high-signal news application built specifically for developers, AI engineers, researchers, and technical leaders. Traditional news aggregators focus on consumer hype, financial speculation, and marketing press releases. DeepIndex AI eliminates noise by prioritizing technical depth, open-weight availability, runnable kernels, and enterprise case studies.

---

## 2. Target Persona & User Stories
### Primary Personas
1. **The Machine Learning / Systems Engineer:** Cares about kernel optimizations (CUDA, FlashAttention), inference frame rate (TFLOPS), vector indexing, and low-level ML hardware.
2. **The Full-Stack / AI App Developer:** Cares about SDK updates, vLLM/LiteLLM release notes, RAG architecture patterns, and open-source GitHub repositories.
3. **The AI Product Leader / CTO:** Cares about real-world enterprise case studies, legal compliance (EU AI Act), security advisories, and model leaderboards.

### Core User Stories
* *As a developer*, I want to scan 15-second summaries and runnable code previews directly on my feed card so I can evaluate tools without reading a 20-page paper.
* *As an ML engineer*, I want news categorized by technical depth (`L1 Overview`, `L2 Deep Dive`, `L3 Kernel/Math`) so I can jump straight to low-level implementation details.
* *As a CTO*, I want to read verified enterprise case studies showing real-world ROI and compliance implications without marketing fluff.

---

## 3. Key Feature Specifications

### 3.1. Unified Micro-Learning Feed
* **Home Feed:** Features a top "Industry Pulse" briefing, enterprise case studies, legal/cybersecurity alerts, and high-impact dev highlights.
* **Category Tabs:** Fast horizontal switching across `⚡ Dev Feed`, `🏢 Case Studies`, `🛡️ CyberSec`, `⚖️ Compliance`, `🛠️ Stack & GPU`, and `🔬 Models`.
* **Zero-Latency Offline Caching:** All feeds are pre-rendered into static JSON and synced locally via Room DB / SQLite for instant renders.

### 3.2. Bold Developer Card Component
* **Source & Depth Badging:** Clear indicator of source domain + depth badge (`L1`, `L2`, or `L3`).
* **In-Card Segmented Controls:**
  * **Summary:** 2–3 sentence executive summary focused on developer utility.
  * **Takeaways:** Bulleted list of architectural breakthroughs, benchmarks, and API changes.
  * **Kernel / Code:** Syntax-highlighted Python/CUDA snippet with a 1-tap copy button.
* **Metric Highlight Pill:** Highlighting performance metrics (e.g., `MMLU 88.5%`, `740 TFLOPS`, `Cycle Time -40%`).
* **Click-to-Expand Modal:** Tapping the card frame slides up a detail modal for full analysis and external GitHub/arXiv links.

### 3.3. Zero-Cost Edge Ingestion Engine
* Fully automated background worker (GitHub Actions + Python) running every 3 hours to fetch arXiv APIs, GitHub releases, and engineering blogs.
* Free LLM processing via Google Gemini 2.5 Flash API with strict JSON schemas.

---

## 4. UI/UX Design Philosophy: "Big, Bold Techie Micro-Learn"

The visual design abandons generic, sterile blog layouts in favor of an energetic, high-contrast, **gamified micro-learning interface** (inspired by Duolingo, Brilliant, and JetBrains IDEs).

```
┌─────────────────────────────────────────────────────────────────┐
│                    UI PHILOSOPHY PILLARS                        │
├─────────────────────────────────────────────────────────────────┤
│ 1. HIGH DENSITY, LOW FRICTION                                  │
│    • Direct answer upfront (TL;DR first, background later).      │
│    • Zero unnecessary page jumps—everything expands inline.     │
│                                                                 │
│ 2. HEAVY TYPOGRAPHY & NEO-BRUTALIST TONES                       │
│    • Heavy font weights (Plus Jakarta Sans 900 / JetBrains Mono)│
│    • Slate-950 dark canvas with solid borders & offset shadows.  │
│                                                                 │
│ 3. CODE AS A FIRST-CLASS CITIZEN                                │
│    • Code blocks are treated as primary visual anchors.         │
│    • Monospace typography with explicit syntax color hierarchy. │
└─────────────────────────────────────────────────────────────────┘
```

### Color Palette (Tailwind Spec)
* **Background Canvas:** Dark Slate (`#020617` / `slate-950`)
* **Card Containers:** Deep Navy (`#0f172a` / `slate-900`) with Slate-800 borders (`#1e293b`)
* **Primary Brand Accent:** Electric Blue (`#2563eb`) & Cyan (`#22d3ee`)
* **Attention / Benchmark Accent:** Bright Yellow (`#facc15`)
* **Security / Warning Accent:** Rose Red (`#fb7185`)
* **Hardware & Code Accent:** Emerald Green (`#34d399`)