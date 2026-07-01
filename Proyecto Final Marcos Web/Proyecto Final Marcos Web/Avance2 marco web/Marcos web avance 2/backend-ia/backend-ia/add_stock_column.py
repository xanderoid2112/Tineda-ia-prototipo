from db import db
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def add_stock_column():
    try:
        logger.info("Adding stock column to productos table...")
        query = "ALTER TABLE productos ADD COLUMN IF NOT EXISTS stock INTEGER DEFAULT 0;"
        db.execute_query(query)
        logger.info("Stock column added successfully.")
    except Exception as e:
        logger.error(f"Error adding stock column: {e}")

if __name__ == "__main__":
    add_stock_column()
