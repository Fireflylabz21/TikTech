import os
import psycopg2

db_url = "postgresql://neondb_owner:npg_7LpXeiCUR0Ea@ep-crimson-haze-b5gnw1sj-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
try:
    conn = psycopg2.connect(db_url)
    cur = conn.cursor()
    cur.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema='public';
    """)
    tables = [row[0] for row in cur.fetchall()]
    print("Existing Tables in Neon DB:", tables)

    # Check privileges
    cur.execute("""
        SELECT grantee, privilege_type
        FROM information_schema.table_privileges
        WHERE table_schema='public';
    """)
    privileges = cur.fetchall()
    print("Table Privileges:", privileges[:10])

    cur.close()
    conn.close()
except Exception as e:
    print("Connection Error:", e)
