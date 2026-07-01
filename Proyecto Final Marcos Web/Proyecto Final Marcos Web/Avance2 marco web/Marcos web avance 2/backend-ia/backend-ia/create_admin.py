import psycopg2
from config import config

try:
    print(f"Conectando a: {config.DATABASE_URL}")
    conn = psycopg2.connect(config.DATABASE_URL)
    cur = conn.cursor()

    # Verificar si existe el usuario admin
    cur.execute("SELECT id, email, rol FROM usuarios WHERE email = 'admin@aimarket.com'")
    admin = cur.fetchone()

    if admin:
        print(f"El usuario admin ya existe: ID={admin[0]}, Email={admin[1]}, Rol={admin[2]}")
        # Actualizar contraseña y rol siempre para asegurar acceso
        password_hash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7.odEqJ62" # admin123
        
        cur.execute("UPDATE usuarios SET rol = 'ADMIN', contrasena = %s WHERE id = %s", (password_hash, admin[0]))
        conn.commit()
        print("Contraseña y Rol actualizados correctamente.")
        print("Nueva Contraseña: admin123")
    else:
        # Crear usuario admin
        # La contraseña es 'admin123' hasheada con BCrypt (cost 10)
        password_hash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7.odEqJ62" 
        
        cur.execute("""
            INSERT INTO usuarios (nombre, email, contrasena, rol)
            VALUES ('Administrador', 'admin@aimarket.com', %s, 'ADMIN')
        """, (password_hash,))
        conn.commit()
        print("Usuario admin creado exitosamente.")
        print("Email: admin@aimarket.com")
        print("Password: admin123")

    cur.close()
    conn.close()

except Exception as e:
    print(f"Error: {e}")
