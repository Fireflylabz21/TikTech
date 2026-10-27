import os
import psycopg2

db_url = "postgresql://neondb_owner:npg_7LpXeiCUR0Ea@ep-crimson-haze-b5gnw1sj-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require"

try:
    conn = psycopg2.connect(db_url)
    conn.autocommit = True
    cur = conn.cursor()

    with open("neon_db_schema.sql", "r", encoding="utf-8") as f:
        schema_sql = f.read()

    cur.execute(schema_sql)
    print("Successfully applied neon_db_schema.sql to Neon DB!")

    # Verify tables again
    cur.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema='public';
    """)
    tables = [row[0] for row in cur.fetchall()]
    print("Updated Tables in Neon DB:", tables)

    cur.close()
    conn.close()
except Exception as e:
    print("Schema Application Error:", e)
