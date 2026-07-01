import psycopg2
from psycopg2.extras import RealDictCursor
import logging
from config import config

logger = logging.getLogger(__name__)

class DatabaseConnection:
    def __init__(self):
        self.conn = None
    
    def get_connection(self):
        """Obtiene conexión a la base de datos"""
        try:
            if self.conn is None or self.conn.closed:
                logger.info(f"Conectando a base de datos: {config.DATABASE_URL}")
                self.conn = psycopg2.connect(
                    config.DATABASE_URL,
                    cursor_factory=RealDictCursor
                )
                logger.info("Conexión establecida exitosamente")
            return self.conn
        except Exception as e:
            logger.error(f"Error conectando a base de datos: {e}")
            logger.error(f"URL de conexión: {config.DATABASE_URL}")
            raise
    
    def execute_query(self, query, params=None):
        """Ejecuta consulta y retorna resultados"""
        conn = self.get_connection()
        try:
            with conn.cursor() as cur:
                cur.execute(query, params or ())
                
                # Capturar resultados si la query retorna algo (SELECT o RETURNING)
                result = None
                if cur.description:
                    result = cur.fetchall()
                
                # Hacer commit si no es un SELECT puro (para guardar cambios de INSERT/UPDATE)
                if not query.strip().upper().startswith('SELECT'):
                    conn.commit()
                
                # Retornar resultados si existen, sino el rowcount
                if result is not None:
                    return result
                return cur.rowcount
        except Exception as e:
            conn.rollback()
            logger.error(f"Error ejecutando query: {e}")
            raise
    
    def close(self):
        """Cierra la conexión"""
        if self.conn and not self.conn.closed:
            self.conn.close()
            logger.info("Conexión cerrada")

# Instancia global de base de datos
db = DatabaseConnection()