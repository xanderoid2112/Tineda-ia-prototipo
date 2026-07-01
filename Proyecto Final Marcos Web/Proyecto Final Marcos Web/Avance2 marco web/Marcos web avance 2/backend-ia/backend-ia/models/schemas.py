from pydantic import BaseModel, Field
from typing import List, Optional

class Categoria(BaseModel):
    id: int
    nombre: str
    slug: str

class CategoriaCreate(BaseModel):
    nombre: str
    slug: Optional[str] = None

class CategoriaUpdate(BaseModel):
    nombre: Optional[str] = None
    slug: Optional[str] = None

class Producto(BaseModel):
    id: int
    nombre: str
    categoria_id: Optional[int] = None
    categoria_nombre: Optional[str] = None
    categoria_slug: Optional[str] = None
    precio: float
    stock: int = 0
    marcas: Optional[str] = None
    marca: Optional[str] = None
    tipo: Optional[str] = None
    dieta: Optional[str] = None
    url_imagen: Optional[str] = None

class ProductoCreate(BaseModel):
    nombre: str
    precio: float
    stock: int = 0
    marcas: Optional[str] = None
    tipo: Optional[str] = None
    dieta: Optional[str] = None
    url_imagen: Optional[str] = None
    categoria_id: Optional[int] = None

class ProductoUpdate(BaseModel):
    nombre: Optional[str] = None
    precio: Optional[float] = None
    stock: Optional[int] = None
    marcas: Optional[str] = None
    tipo: Optional[str] = None
    dieta: Optional[str] = None
    url_imagen: Optional[str] = None
    categoria_id: Optional[int] = None

class PreferenciasUsuario(BaseModel):
    marcas_preferidas: List[str] = Field(default_factory=list)
    dietas: List[str] = Field(default_factory=list)
    categorias_excluidas: List[str] = Field(default_factory=list)

class CanastaRequest(BaseModel):
    usuario_id: int
    presupuesto: float = Field(gt=0, description="El presupuesto debe ser mayor a 0")
    productos_deseados: List[int] = Field(default_factory=list)
    preferencias: Optional[PreferenciasUsuario] = None

class ProductoCanasta(BaseModel):
    id: int
    nombre: str
    precio: float
    marcas: Optional[str] = None
    marca: Optional[str] = None
    tipo: Optional[str] = None
    dieta: Optional[str] = None
    url_imagen: Optional[str] = None
    categoria_id: Optional[int] = None
    categoria_nombre: Optional[str] = None
    categoria_slug: Optional[str] = None
    es_deseado: bool = False
    score_relevancia: Optional[float] = None

class CanastaResponse(BaseModel):
    productos: List[ProductoCanasta]
    total: float
    presupuesto: float
    ahorro: float
    porcentaje_ahorro: float
    productos_recomendados: int
    productos_deseados: int