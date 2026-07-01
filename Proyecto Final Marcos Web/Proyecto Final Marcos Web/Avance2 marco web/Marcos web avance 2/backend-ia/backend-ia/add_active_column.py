import os
import psycopg2
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")

def add_active_column():
    try:
        conn = psycopg2.connect(DATABASE_URL)
        cursor = conn.cursor()
        
        print("Conectado a la base de datos...")
        
        # Agregar columna activo si no existe
        cursor.execute("""
            ALTER TABLE productos 
            ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
        """)
        
        conn.commit()
        print("Columna 'activo' agregada o verificada exitosamente.")
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    add_active_column()
