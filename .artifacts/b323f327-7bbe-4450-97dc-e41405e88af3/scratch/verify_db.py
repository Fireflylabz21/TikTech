import psycopg2

db_url = "postgresql://neondb_owner:npg_7LpXeiCUR0Ea@ep-crimson-haze-b5gnw1sj-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require"

try:
    conn = psycopg2.connect(db_url)
    cur = conn.cursor()

    # 1. List tables
    cur.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema='public';
    """)
    tables = [row[0] for row in cur.fetchall()]
    print("Tables in Neon DB:", tables)

    # 2. Count rows in articles
    cur.execute("SELECT COUNT(*) FROM articles;")
    article_count = cur.fetchone()[0]
    print(f"Total Articles in DB: {article_count}")

    # 3. Count rows in article_embeddings
    cur.execute("SELECT COUNT(*) FROM article_embeddings;")
    embedding_count = cur.fetchone()[0]
    print(f"Total Embeddings in DB: {embedding_count}")

    # 4. Count rows in entity_nodes and knowledge_graph_triplets
    cur.execute("SELECT COUNT(*) FROM entity_nodes;")
    node_count = cur.fetchone()[0]
    print(f"Total Entity Nodes in KG: {node_count}")

    cur.execute("SELECT COUNT(*) FROM knowledge_graph_triplets;")
    triplet_count = cur.fetchone()[0]
    print(f"Total KG Triplets: {triplet_count}")

    # 5. Check if search_articles function exists
    cur.execute("""
        SELECT routine_name
        FROM information_schema.routines
        WHERE routine_schema='public' AND routine_name='search_articles';
    """)
    func = cur.fetchone()
    print("Search Articles RPC Function Exists:", func is not None)

    cur.close()
    conn.close()
    print("Database verification completed successfully!")
except Exception as e:
    print("Verification Error:", e)
