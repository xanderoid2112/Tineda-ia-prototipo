import psycopg2
from config import config

def fix_schema():
    try:
        conn = psycopg2.connect(config.DATABASE_URL)
        cur = conn.cursor()
        
        print("Checking 'compras' table schema...")
        
        # Check if table exists
        cur.execute("SELECT to_regclass('public.compras')")
        if not cur.fetchone()[0]:
            print("Table 'compras' does not exist. Creating it...")
            cur.execute("""
                CREATE TABLE compras (
                    id SERIAL PRIMARY KEY,
                    usuario_id INT,
                    total DECIMAL(10, 2),
                    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    estado VARCHAR(50) DEFAULT 'pendiente',
                    items JSONB,
                    subtotal DECIMAL(10, 2),
                    costo_envio DECIMAL(10, 2)
                )
            """)
            conn.commit()
            print("Table created.")
            return

        # Check columns
        cur.execute("""
            SELECT column_name 
            FROM information_schema.columns 
            WHERE table_name = 'compras'
        """)
        columns = [row[0] for row in cur.fetchall()]
        print(f"Existing columns: {columns}")
        
        # Add missing columns
        if 'items' not in columns:
            print("Adding 'items' column...")
            cur.execute("ALTER TABLE compras ADD COLUMN items JSONB")
            
        if 'subtotal' not in columns:
            print("Adding 'subtotal' column...")
            cur.execute("ALTER TABLE compras ADD COLUMN subtotal DECIMAL(10, 2)")
            
        if 'costo_envio' not in columns:
            print("Adding 'costo_envio' column...")
            cur.execute("ALTER TABLE compras ADD COLUMN costo_envio DECIMAL(10, 2)")
            
        conn.commit()
        print("Schema update complete.")
        
    except Exception as e:
        print(f"Error: {e}")
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    fix_schema()
