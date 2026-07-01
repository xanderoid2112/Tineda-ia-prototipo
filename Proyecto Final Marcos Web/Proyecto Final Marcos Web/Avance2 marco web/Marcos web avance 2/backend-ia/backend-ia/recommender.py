import random
import logging
from typing import List, Dict, Any, Optional
from db import db

logger = logging.getLogger(__name__)

def generar_canasta(usuario_id: int, presupuesto: float, productos_deseados: List[int] = None, preferencias_usuario: Dict = None) -> Dict[str, Any]:
    """
    Genera una canasta optimizada usando IA basada en:
    - Presupuesto del usuario
    - Productos deseados específicos
    - Preferencias de marcas, dietas y categorías
    """
    try:
        logger.info(f"Generando canasta para usuario {usuario_id} con presupuesto {presupuesto}")
        
        # Obtener productos de la base de datos
        productos = obtener_productos_disponibles()
        
        if not productos:
            return crear_respuesta_vacia(presupuesto)
        
        logger.info(f"Total productos disponibles: {len(productos)}")
        
        # Aplicar filtros de preferencias (manejar errores silenciosamente)
        try:
            productos_filtrados = aplicar_filtros_preferencias(productos, preferencias_usuario)
            logger.info(f"Productos después de filtrar: {len(productos_filtrados)}")
        except Exception as e:
            logger.warning(f"Error aplicando filtros: {e}, usando todos los productos")
            productos_filtrados = productos
        
        # Obtener historial de compras del usuario para mejorar recomendaciones
        try:
            historial_compras = obtener_historial_usuario(usuario_id)
            logger.info(f"Historial de compras: {historial_compras}")
        except Exception as e:
            logger.warning(f"Error obteniendo historial: {e}, usando simulado")
            historial_compras = [1, 2, 3, 7, 11]
        
        # Incluir productos deseados primero
        canasta_productos = []
        costo_actual = 0.0
        
        if productos_deseados:
            for producto_id in productos_deseados:
                producto = next((p for p in productos_filtrados if p['id'] == producto_id), None)
                if producto and costo_actual + producto['precio'] <= presupuesto:
                    canasta_productos.append({
                        **producto,
                        'es_deseado': True,
                        'score_relevancia': 1.0
                    })
                    costo_actual += producto['precio']
        
        # Priorizar productos del historial si no hay productos deseados
        productos_prioritarios = []
        if not productos_deseados and historial_compras:
            for producto_id in historial_compras[:3]:  # Top 3 productos más comprados
                producto = next((p for p in productos_filtrados if p['id'] == producto_id), None)
                if producto and costo_actual + producto['precio'] <= presupuesto:
                    productos_prioritarios.append({
                        **producto,
                        'es_deseado': False,
                        'es_del_historial': True,
                        'score_relevancia': 0.9
                    })
                    costo_actual += producto['precio']
        
        canasta_productos.extend(productos_prioritarios)
        
        # Generar recomendaciones usando algoritmo de IA
        presupuesto_restante = presupuesto - costo_actual
        logger.info(f"Presupuesto restante: {presupuesto_restante}")
        logger.info(f"Productos en canasta actual: {len(canasta_productos)}")
        
        productos_recomendados = generar_recomendaciones_ia(
            productos_filtrados, 
            canasta_productos, 
            presupuesto_restante,
            usuario_id,
            historial_compras
        )
        
        logger.info(f"Productos recomendados generados: {len(productos_recomendados)}")
        canasta_productos.extend(productos_recomendados)
        
        # Calcular totales
        total = sum(p['precio'] for p in canasta_productos)
        ahorro = max(0, presupuesto - total)
        porcentaje_ahorro = (ahorro / presupuesto * 100) if presupuesto > 0 else 0
        
        return {
            "productos": canasta_productos,
            "total": round(total, 2),
            "presupuesto": presupuesto,
            "ahorro": round(ahorro, 2),
            "porcentaje_ahorro": round(porcentaje_ahorro, 2),
            "productos_recomendados": len([p for p in canasta_productos if not p.get('es_deseado', False)]),
            "productos_deseados": len([p for p in canasta_productos if p.get('es_deseado', False)])
        }
        
    except Exception as e:
        logger.error(f"Error generando canasta: {e}")
        return crear_respuesta_vacia(presupuesto)

def obtener_productos_disponibles() -> List[Dict]:
    """Obtiene todos los productos disponibles de la base de datos"""
    try:
        query = """
        SELECT 
            p.id,
            p.nombre,
            p.precio,
            p.marcas,
            p.dieta,
            p.url_imagen,
            p.categoria_id,
            p.stock,
            c.nombre AS categoria_nombre,
            c.slug AS categoria_slug
        FROM productos p
        LEFT JOIN categorias c ON p.categoria_id = c.id
        ORDER BY precio ASC
        """
        productos_db = db.execute_query(query)
        logger.info(f"Productos obtenidos de BD: {len(productos_db) if productos_db else 0}")
        if productos_db and len(productos_db) > 0:
            # Normalizar precios DECIMAL a float para evitar conflictos con operaciones aritméticas
            productos_normalizados = []
            for p in productos_db:
                try:
                    pn = dict(p)
                    if pn.get('precio') is not None:
                        pn['precio'] = float(pn['precio'])
                    
                    # Normalizar marca y marcas
                    marca_val = pn.get('marcas') or pn.get('marca')
                    pn['marca'] = marca_val
                    pn['marcas'] = marca_val
                    
                    # Normalizar stock
                    pn['stock'] = pn.get('stock') or 0
                    
                    categoria_nombre = pn.get('categoria_nombre') or pn.get('categoria')
                    categoria_slug = pn.get('categoria_slug')
                    if categoria_nombre and not categoria_slug:
                        categoria_slug = categoria_nombre.lower().replace(' ', '-')
                    pn['categoria_nombre'] = categoria_nombre
                    pn['categoria_slug'] = categoria_slug
                    pn['categoria'] = categoria_nombre or ''
                    productos_normalizados.append(pn)
                except Exception as e:
                    logger.warning(f"Error procesando producto: {e}")
                    continue
            return productos_normalizados
        # Si la consulta no retorna productos, significa que la tabla está vacía
        logger.error("La tabla productos está vacía")
        return []
    except Exception as e:
        logger.error(f"Error conectando a la base de datos: {e}")
        logger.error("NO se usarán productos de ejemplo - el sistema requiere conexión a BD")
        # NO retornar productos de ejemplo - retornar lista vacía
        return []

import json

def obtener_historial_usuario(usuario_id: int) -> List[int]:
    """Obtiene el historial de compras del usuario desde la tabla compras"""
    try:
        query = """
        SELECT items
        FROM compras
        WHERE usuario_id = %s AND items IS NOT NULL
        ORDER BY fecha_compra DESC
        LIMIT 10
        """
        result = db.execute_query(query, (usuario_id,))
        
        # Lista de IDs de productos de fallback que realmente existen en la base de datos
        fallback_ids = [69, 70, 40, 44, 66, 39, 62, 68]
        
        if not result:
            return fallback_ids
            
        producto_counts = {}
        for row in result:
            items = row.get('items', [])
            if isinstance(items, str):
                try:
                    items = json.loads(items)
                except Exception:
                    continue
            if isinstance(items, list):
                for item in items:
                    pid = item.get('producto_id')
                    if pid:
                        producto_counts[int(pid)] = producto_counts.get(int(pid), 0) + int(item.get('cantidad', 1))
        
        # Ordenar por frecuencia descendente
        productos_ordenados = sorted(producto_counts.items(), key=lambda x: x[1], reverse=True)
        historial_ids = [p[0] for p in productos_ordenados]
        
        if not historial_ids:
            return fallback_ids
            
        return historial_ids
    except Exception as e:
        logger.error(f"Error obteniendo historial: {e}")
        return [69, 70, 40, 44, 66, 39, 62, 68]

def aplicar_filtros_preferencias(productos: List[Dict], preferencias: Dict = None) -> List[Dict]:
    """Aplica filtros basados en las preferencias del usuario"""
    if not preferencias:
        return productos
    
    productos_filtrados = productos.copy()
    
    # Filtrar por marcas preferidas (priorizar, no forzar)
    if preferencias.get('marcas_preferidas') and len(preferencias['marcas_preferidas']) > 0:
        marcas_preferidas = [m.lower() for m in preferencias['marcas_preferidas']]
        productos_con_marca = [p for p in productos_filtrados if p.get('marca') and p['marca'].lower() in marcas_preferidas]
        # Si hay productos con la marca preferida, priorizarlos pero agregar más productos también
        if len(productos_con_marca) > 0:
            # Agregar productos de marca preferida AL INICIO
            otros_productos = [p for p in productos_filtrados if p not in productos_con_marca]
            productos_filtrados = productos_con_marca + otros_productos
    
    # Filtrar por dietas (solo excluir productos NO compatibles)
    if preferencias.get('dietas') and len(preferencias['dietas']) > 0:
        dietas = [d.lower() for d in preferencias['dietas']]
        normalized_dietas = []
        for d in dietas:
            if d in ['vegano', 'vegana']:
                normalized_dietas.extend(['vegano', 'vegana'])
            elif d in ['sin_gluten', 'sin gluten', 'gluten free']:
                normalized_dietas.extend(['sin_gluten', 'sin gluten'])
            elif d in ['sin_lactosa', 'sin lactosa', 'lactose free']:
                normalized_dietas.extend(['sin_lactosa', 'sin lactosa'])
            elif d in ['vegetariano', 'vegetariana']:
                normalized_dietas.extend(['vegetariano', 'vegetariana'])
            else:
                normalized_dietas.append(d)
                
        productos_filtrados = [
            p for p in productos_filtrados 
            if not p.get('dieta') or p['dieta'].lower() in normalized_dietas  # Productos sin dieta o con dieta compatible
        ]
    
    # Excluir categorías
    if preferencias.get('categorias_excluidas'):
        categorias_excluidas = [c.lower() for c in preferencias['categorias_excluidas']]
        productos_filtrados = [
            p for p in productos_filtrados 
            if all(
                (valor or '').lower() not in categorias_excluidas
                for valor in [
                    p.get('categoria'),
                    p.get('categoria_nombre'),
                    p.get('categoria_slug')
                ]
            )
        ]
    
    # Asegurar que hay productos disponibles
    if len(productos_filtrados) == 0:
        logger.warning("No hay productos con las preferencias, usando todos los productos")
        return productos
    
    return productos_filtrados

def generar_recomendaciones_ia(productos: List[Dict], canasta_actual: List[Dict], presupuesto_restante: float, usuario_id: int, historial: List[int] = None) -> List[Dict]:
    """
    Genera recomendaciones usando scoring real de IA:
    - Diversidad de categorías (penaliza categorías repetidas)
    - Historial del usuario (prioriza productos ya comprados)
    - Relación precio/valor
    - Máximo 2 productos por categoría
    """
    logger.info(f"Iniciando IA - Productos: {len(productos)}, Presupuesto restante: {presupuesto_restante}")

    if presupuesto_restante <= 0 or len(productos) == 0:
        return []

    # Contar categorías ya en la canasta actual
    categorias_incluidas = {}
    for p in canasta_actual:
        cat = p.get('categoria', '') or p.get('categoria_nombre', '')
        categorias_incluidas[cat] = categorias_incluidas.get(cat, 0) + 1

    # Calcular score para cada producto
    productos_con_score = []
    for producto in productos:
        score = calcular_score_relevancia(producto, canasta_actual, usuario_id, historial)

        # Penalizar categorías ya muy representadas en la canasta
        cat = producto.get('categoria', '') or producto.get('categoria_nombre', '')
        veces = categorias_incluidas.get(cat, 0)
        if veces >= 2:
            score *= 0.3
        elif veces == 1:
            score *= 0.65

        productos_con_score.append({**producto, '_score': score})

    # Ordenar por score descendente
    productos_con_score.sort(key=lambda x: x['_score'], reverse=True)

    recomendaciones = []
    categorias_recomendadas = {}

    for producto in productos_con_score:
        precio = producto['precio']
        cat = producto.get('categoria', '') or producto.get('categoria_nombre', '')

        if precio > presupuesto_restante:
            continue

        # Máximo 2 productos por categoría en recomendaciones
        if categorias_recomendadas.get(cat, 0) >= 2:
            continue

        score_final = producto['_score']
        recomendaciones.append({
            **{k: v for k, v in producto.items() if k != '_score'},
            'es_deseado': False,
            'score_relevancia': round(min(score_final, 1.0), 3)
        })
        presupuesto_restante -= precio
        categorias_recomendadas[cat] = categorias_recomendadas.get(cat, 0) + 1

        if len(recomendaciones) >= 20:
            break

    cats_distintas = len(set(p.get('categoria', '') or p.get('categoria_nombre', '') for p in recomendaciones))
    logger.info(f"IA generó {len(recomendaciones)} productos de {cats_distintas} categorías distintas")
    return recomendaciones

def calcular_score_relevancia(producto: Dict, canasta_actual: List[Dict], usuario_id: int, historial: List[int] = None) -> float:
    """Calcula score de relevancia real para el producto"""
    score = 0.4  # Score base

    # Bonus por diversidad: si la categoría no está en la canasta actual
    categorias_actuales = [p.get('categoria', '') or p.get('categoria_nombre', '') for p in canasta_actual]
    cat = producto.get('categoria', '') or producto.get('categoria_nombre', '')
    if cat not in categorias_actuales:
        score += 0.3

    # Bonus por precio accesible
    precio = producto.get('precio', 0)
    if precio < 5:
        score += 0.25
    elif precio < 15:
        score += 0.15
    elif precio < 30:
        score += 0.05

    # Bonus por stock disponible
    stock = producto.get('stock', 0) or 0
    if stock > 20:
        score += 0.15
    elif stock > 5:
        score += 0.08

    # Bonus fuerte por historial del usuario (productos que ya compró)
    if historial and producto.get('id') in historial:
        score += 0.4
        if producto.get('id') in historial[:3]:  # Top 3 más comprados
            score += 0.2

    return min(1.0, score)

def crear_respuesta_vacia(presupuesto: float) -> Dict[str, Any]:
    """Crea una respuesta vacía cuando no se pueden generar recomendaciones"""
    return {
        "productos": [],
        "total": 0.0,
        "presupuesto": presupuesto,
        "ahorro": presupuesto,
        "porcentaje_ahorro": 100.0,
        "productos_recomendados": 0,
        "productos_deseados": 0
    }
