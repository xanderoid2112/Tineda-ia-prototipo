from services.product_service import ProductService
from .lightfm_recommender import LightFMRecommender
import logging
from typing import List, Dict
import pandas as pd

logger = logging.getLogger(__name__)

class RecommenderSystem:
    def __init__(self):
        self.product_service = ProductService()
        self.lightfm_recommender = LightFMRecommender()
    
    def generar_canasta_inteligente(self, usuario_id: int, presupuesto: float, 
                                  productos_deseados: List[int] = None,
                                  preferencias_usuario: Dict = None) -> Dict:
        """Genera canasta optimizada usando IA"""
        try:
            # Obtener preferencias (combinar las enviadas con las guardadas)
            preferencias_guardadas = self.product_service.obtener_preferencias_usuario(usuario_id)
            preferencias = self._combinar_preferencias(preferencias_guardadas, preferencias_usuario)
            
            # Obtener productos filtrados
            productos = self.product_service.obtener_productos_filtrados(
                marcas=preferencias.get('marcas_preferidas'),
                dietas=preferencias.get('dietas'),
                categorias_excluidas=preferencias.get('categorias_excluidas')
            )
            
            if not productos:
                return self._generar_canasta_fallback(presupuesto)
            
            # Generar recomendaciones
            productos_df = pd.DataFrame(productos)
            productos_ids = productos_df['id'].tolist()
            
            # Obtener scores de recomendación
            recomendaciones = self.lightfm_recommender.predecir_recomendaciones(usuario_id, productos_ids)
            
            # Optimizar canasta
            canasta_optimizada = self._optimizar_canasta(
                productos_df, recomendaciones, presupuesto, productos_deseados
            )
            
            return self._formatear_respuesta(canasta_optimizada, presupuesto)
            
        except Exception as e:
            logger.error(f"Error generando canasta inteligente: {e}")
            return self._generar_canasta_fallback(presupuesto)
    
    def _combinar_preferencias(self, guardadas: Dict, nuevas: Dict) -> Dict:
        """Combina preferencias guardadas con las nuevas de la solicitud"""
        if not nuevas:
            return guardadas
        
        resultado = guardadas.copy()
        
        # Combinar listas, evitando duplicados
        for key in ['marcas_preferidas', 'dietas', 'categorias_excluidas']:
            if key in nuevas and nuevas[key]:
                resultado[key] = list(set(resultado.get(key, []) + nuevas[key]))
        
        return resultado
    
    def _optimizar_canasta(self, productos_df: pd.DataFrame, recomendaciones: Dict[int, float],
                          presupuesto: float, productos_deseados: List[int] = None) -> List[Dict]:
        """Algoritmo de optimización mejorado"""
        canasta = []
        total_actual = 0.0
        
        # Preparar datos de productos
        productos_data = []
        for _, producto in productos_df.iterrows():
            score = recomendaciones.get(producto['id'], 0.5)
            es_deseado = productos_deseados and producto['id'] in productos_deseados
            
            productos_data.append({
                'id': producto['id'],
                'nombre': producto['nombre'],
                'precio': float(producto['precio']),
                'marca': producto['marca'],
                'dieta': producto['dieta'],
                'categoria': producto['categoria'],
                'url_imagen': producto['url_imagen'],
                'score': score,
                'es_deseado': es_deseado,
                'valor_por_dinero': score / producto['precio'] if producto['precio'] > 0 else 0
            })
        
        # Primero agregar productos deseados
        if productos_deseados:
            for producto in productos_data:
                if producto['es_deseado'] and total_actual + producto['precio'] <= presupuesto:
                    canasta.append(producto)
                    total_actual += producto['precio']
        
        # Luego agregar productos optimizados por valor
        productos_restantes = [p for p in productos_data if p not in canasta]
        productos_restantes.sort(key=lambda x: x['valor_por_dinero'], reverse=True)
        
        for producto in productos_restantes:
            if total_actual + producto['precio'] <= presupuesto:
                canasta.append(producto)
                total_actual += producto['precio']
        
        return canasta
    
    def _formatear_respuesta(self, canasta: List[Dict], presupuesto: float) -> Dict:
        """Formatea la respuesta final"""
        total = sum(item['precio'] for item in canasta)
        ahorro = presupuesto - total
        
        return {
            "productos": [{
                "id": item['id'],
                "nombre": item['nombre'],
                "precio": item['precio'],
                "marca": item['marca'],
                "dieta": item['dieta'],
                "url_imagen": item['url_imagen'],
                "categoria": item['categoria'],
                "es_deseado": item.get('es_deseado', False),
                "score_relevancia": item.get('score', 0.5)
            } for item in canasta],
            "total": total,
            "presupuesto": presupuesto,
            "ahorro": ahorro if ahorro > 0 else 0,
            "porcentaje_ahorro": (ahorro / presupuesto) * 100 if ahorro > 0 else 0,
            "productos_recomendados": len([p for p in canasta if not p.get('es_deseado', False)]),
            "productos_deseados": len([p for p in canasta if p.get('es_deseado', False)])
        }
    
    def _generar_canasta_fallback(self, presupuesto: float) -> Dict:
        """Canasta de respaldo en caso de error"""
        return {
            "productos": [],
            "total": 0,
            "presupuesto": presupuesto,
            "ahorro": presupuesto,
            "porcentaje_ahorro": 100,
            "productos_recomendados": 0,
            "productos_deseados": 0
        }

# Instancia global
recommender_system = RecommenderSystem()

def generar_canasta(usuario_id: int, presupuesto: float, 
                   productos_deseados: List[int] = None,
                   preferencias_usuario: Dict = None) -> Dict:
    """Función principal para generar canasta (mantiene compatibilidad)"""
    return recommender_system.generar_canasta_inteligente(
        usuario_id, presupuesto, productos_deseados, preferencias_usuario
    )