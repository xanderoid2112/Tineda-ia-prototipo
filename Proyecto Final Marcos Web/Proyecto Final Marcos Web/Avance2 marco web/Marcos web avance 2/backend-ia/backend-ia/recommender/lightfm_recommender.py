import pandas as pd
import numpy as np
from lightfm import LightFM
from lightfm.data import Dataset
import pickle
import os
import logging
from typing import List, Dict
from config import config

logger = logging.getLogger(__name__)

class LightFMRecommender:
    def __init__(self):
        self.model = None
        self.dataset = None
        self.is_trained = False
    
    def entrenar_modelo(self, interacciones: pd.DataFrame, 
                       productos: pd.DataFrame, 
                       preferencias: pd.DataFrame = None):
        """Entrena el modelo LightFM con datos históricos"""
        try:
            # Preparar dataset
            self.dataset = Dataset()
            self.dataset.fit(
                users=interacciones['usuario_id'].unique(),
                items=interacciones['producto_id'].unique(),
                item_features=productos['categoria'].unique()
            )
            
            # Construir matriz de interacciones
            interacciones_matrix, _ = self.dataset.build_interactions(
                [(row['usuario_id'], row['producto_id'], row['frecuencia']) 
                 for _, row in interacciones.iterrows()]
            )
            
            # Entrenar modelo
            self.model = LightFM(
                loss='warp',
                no_components=20,
                learning_rate=0.05,
                item_alpha=1e-6
            )
            
            self.model.fit(interacciones_matrix, epochs=20, num_threads=4, verbose=True)
            self.is_trained = True
            
            # Guardar modelo
            self.guardar_modelo()
            logger.info("Modelo LightFM entrenado exitosamente")
            
        except Exception as e:
            logger.error(f"Error entrenando modelo: {e}")
            raise
    
    def predecir_recomendaciones(self, usuario_id: int, productos_ids: List[int]) -> Dict[int, float]:
        """Genera scores de recomendación para un usuario"""
        if not self.is_trained or self.model is None:
            return self._recomendaciones_por_defecto(productos_ids)
        
        try:
            # Convertir IDs a índices internos
            user_internal_id = self.dataset.mapping()[0].get(usuario_id)
            if user_internal_id is None:
                return self._recomendaciones_por_defecto(productos_ids)
            
            item_internal_ids = [self.dataset.mapping()[2].get(pid) for pid in productos_ids]
            item_internal_ids = [iid for iid in item_internal_ids if iid is not None]
            
            if not item_internal_ids:
                return self._recomendaciones_por_defecto(productos_ids)
            
            # Predecir scores
            scores = self.model.predict(user_internal_id, item_internal_ids)
            
            # Mapear de vuelta a IDs originales
            recommendations = {}
            internal_to_external = {v: k for k, v in self.dataset.mapping()[2].items()}
            
            for internal_id, score in zip(item_internal_ids, scores):
                external_id = internal_to_external[internal_id]
                recommendations[external_id] = float(score)
            
            return recommendations
            
        except Exception as e:
            logger.error(f"Error en predicción: {e}")
            return self._recomendaciones_por_defecto(productos_ids)
    
    def _recomendaciones_por_defecto(self, productos_ids: List[int]) -> Dict[int, float]:
        """Recomendaciones por defecto basadas en precio (para usuarios nuevos)"""
        return {pid: 1.0 for pid in productos_ids}
    
    def guardar_modelo(self):
        """Guarda el modelo entrenado"""
        os.makedirs(os.path.dirname(config.MODEL_PATH), exist_ok=True)
        with open(config.MODEL_PATH, 'wb') as f:
            pickle.dump({
                'model': self.model,
                'dataset': self.dataset,
                'is_trained': self.is_trained
            }, f)
    
    def cargar_modelo(self):
        """Carga un modelo previamente entrenado"""
        try:
            with open(config.MODEL_PATH, 'rb') as f:
                data = pickle.load(f)
                self.model = data['model']
                self.dataset = data['dataset']
                self.is_trained = data['is_trained']
            logger.info("Modelo LightFM cargado exitosamente")
        except FileNotFoundError:
            logger.warning("Modelo no encontrado, necesita entrenamiento")
            self.is_trained = False