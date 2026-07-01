from db import db
import logging
from typing import List, Dict

logger = logging.getLogger(__name__)

class ProductService:
    def __init__(self):
        self.db = db
    
    def obtener_productos_filtrados(self, marcas: List[str] = None, 
                                  dietas: List[str] = None,
                                  categorias_excluidas: List[str] = None) -> List[Dict]:
        """Obtiene productos filtrados por preferencias"""
        try:
            query = """
                SELECT 
                    p.id,
                    p.nombre,
                    p.precio,
                    p.marca,
                    p.dieta,
                    p.url_imagen,
                    p.categoria_id,
                    c.nombre AS categoria_nombre,
                    c.slug AS categoria_slug
                FROM productos p
                LEFT JOIN categorias c ON p.categoria_id = c.id
                WHERE p.precio > 0
            """
            params = []
            
            # Filtros dinámicos
            if marcas:
                query += " AND (marca = ANY(%s) OR %s = '{}')"
                params.extend([marcas, marcas])
            
            if dietas:
                dietas_normalizadas = [d.lower() for d in dietas if d]
                query += " AND LOWER(COALESCE(dieta, '')) = ANY(%s)"
                params.append(dietas_normalizadas)
            
            if categorias_excluidas:
                categorias_normalizadas = [c.lower() for c in categorias_excluidas if c]
                if categorias_normalizadas:
                    placeholders = ','.join(['%s'] * len(categorias_normalizadas))
                    query += f" AND (COALESCE(LOWER(c.slug), LOWER(c.nombre), '') NOT IN ({placeholders}))"
                    params.extend(categorias_normalizadas)
            
            query += " ORDER BY precio ASC, nombre ASC"
            
            productos = self.db.execute_query(query, params)
            return productos
            
        except Exception as e:
            logger.error(f"Error obteniendo productos: {e}")
            return []
    
    def obtener_historial_usuario(self, usuario_id: int) -> List[Dict]:
        """Obtiene el historial de compras del usuario"""
        try:
            query = """
                SELECT 
                    p.id,
                    p.nombre,
                    p.categoria_id,
                    cat.nombre AS categoria_nombre,
                    cat.slug AS categoria_slug,
                    p.precio,
                    p.marca,
                    p.dieta,
                    COUNT(cd.producto_id) as frecuencia,
                    AVG(cd.cantidad) as avg_cantidad
                FROM productos p
                JOIN canasta_detalle cd ON p.id = cd.producto_id
                JOIN canastas c ON cd.canasta_id = c.id
                LEFT JOIN categorias cat ON p.categoria_id = cat.id
                WHERE c.usuario_id = %s
                GROUP BY p.id, p.nombre, p.categoria_id, cat.nombre, cat.slug, p.precio, p.marca, p.dieta
                ORDER BY frecuencia DESC, avg_cantidad DESC
            """
            return self.db.execute_query(query, (usuario_id,))
        except Exception as e:
            logger.error(f"Error obteniendo historial: {e}")
            return []
    
    def obtener_preferencias_usuario(self, usuario_id: int) -> Dict:
        """Obtiene preferencias guardadas del usuario"""
        try:
            query = """
                SELECT tipo_preferencia, valor 
                FROM preferencias_usuarios 
                WHERE usuario_id = %s
            """
            preferencias = self.db.execute_query(query, (usuario_id,))
            
            result = {
                "marcas_preferidas": [],
                "dietas": [],
                "categorias_excluidas": []
            }
            
            for pref in preferencias:
                if pref['tipo_preferencia'] == 'marca':
                    result["marcas_preferidas"].append(pref['valor'])
                elif pref['tipo_preferencia'] == 'dieta':
                    result["dietas"].append(pref['valor'])
                elif pref['tipo_preferencia'] == 'categoria_excluida':
                    result["categorias_excluidas"].append(pref['valor'])
            
            return result
        except Exception as e:
            logger.error(f"Error obteniendo preferencias: {e}")
            return {}