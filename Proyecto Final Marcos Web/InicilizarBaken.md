# Comandos para Iniciar los Backends

Aquí tienes los comandos exactos para iniciar cada servidor. Ejecútalos en terminales separadas (PowerShell o CMD).

## 1. Backend Java (Spring Boot)

Abre una terminal (PowerShell) en la carpeta raíz y ejecuta:

```powershell
cd "Proyecto Final Marcos Web\Avance2 marco web\AIMarket-main\AIMarket-main\demo\demo"
.\mvnw.cmd spring-boot:run
```
*El servidor estará listo cuando veas "Started SupermercadoApplication".*

---

## 2. Backend Python (IA)

Abre **otra** terminal (PowerShell) en la carpeta raíz y ejecuta:

```powershell
cd "Proyecto Final Marcos Web\Avance2 marco web\Marcos web avance 2\backend-ia\backend-ia"
```

(Solo si es la primera vez) Instala las dependencias:
```powershell
pip install -r requirements.txt
```

Inicia el servidor:
```powershell
python main.py
```
*El servidor estará listo cuando veas "Application startup complete".*
