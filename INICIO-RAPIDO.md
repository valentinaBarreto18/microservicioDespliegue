# 🚀 Guía de Inicio Rápido

## Pasos para ejecutar el sistema completo

### 1️⃣ Prerequisitos
```bash
# Verificar que Docker esté instalado
docker --version

# Verificar que Docker Compose esté instalado
docker-compose --version
```

### 2️⃣ Ejecutar el sistema
```bash
# Desde la raíz del proyecto, ejecutar:
docker-compose up --build
```

⏰ **Espera aproximadamente 2-3 minutos** mientras:
- Se construyen las imágenes Docker
- Se crean las bases de datos PostgreSQL
- Se inicializan los datos de ejemplo
- Se inician los 3 microservicios

### 3️⃣ Verificar que todo esté funcionando

```bash
# En otra terminal, verificar el estado de los servicios:

# API Gateway
curl http://localhost:8080/actuator/health

# Inventory Service
curl http://localhost:8081/actuator/health

# Loan Service
curl http://localhost:8082/actuator/health
```

Todos deben responder con: `{"status":"UP"}`

### 4️⃣ Probar el sistema

#### Opción A: Con Postman
1. Abrir Postman
2. Importar el archivo: `postman/Biblioteca-Microservices.postman_collection.json`
3. Ejecutar las peticiones de ejemplo

#### Opción B: Con curl

**Obtener todos los libros:**
```bash
curl http://localhost:8080/api/books
```

**Obtener un libro específico:**
```bash
curl http://localhost:8080/api/books/1
```

**Buscar libros por autor:**
```bash
curl "http://localhost:8080/api/books/search/author?name=Martin"
```

**Crear un nuevo préstamo:**
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 7,
    "userEmail": "test@example.com",
    "userName": "Usuario de Prueba",
    "dueDate": "2025-11-18",
    "notes": "Mi primer préstamo"
  }'
```

**Ver todos los préstamos:**
```bash
curl http://localhost:8080/api/loans
```

### 5️⃣ Detener el sistema

```bash
# Detener los servicios (mantiene los datos)
docker-compose down

# Detener y eliminar todos los datos
docker-compose down -v
```

### 6️⃣ Ver logs en tiempo real

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f inventory-service
docker-compose logs -f loan-service
docker-compose logs -f api-gateway
```

## 📊 Puertos del sistema

| Servicio | URL | Puerto |
|----------|-----|--------|
| **API Gateway** (usar este) | http://localhost:8080 | 8080 |
| Inventory Service | http://localhost:8081 | 8081 |
| Loan Service | http://localhost:8082 | 8082 |
| PostgreSQL Inventory | localhost:5432 | 5432 |
| PostgreSQL Loan | localhost:5433 | 5433 |

## 🎯 Endpoints principales

### 📚 Gestión de Libros
- `GET /api/books` - Listar todos los libros
- `GET /api/books/{id}` - Ver un libro
- `POST /api/books` - Crear libro
- `PUT /api/books/{id}` - Actualizar libro
- `DELETE /api/books/{id}` - Eliminar libro

### 📖 Gestión de Préstamos
- `GET /api/loans` - Listar todos los préstamos
- `GET /api/loans/user/{email}` - Ver préstamos de un usuario
- `POST /api/loans` - Crear préstamo
- `POST /api/loans/{id}/return` - Devolver libro
- `POST /api/loans/{id}/renew?newDueDate=2025-11-25` - Renovar préstamo

## 🔍 Datos de ejemplo incluidos

El sistema viene con datos de prueba:

**Libros:**
- 10 libros de programación ya cargados
- Categorías: Programación, Arquitectura, Frameworks, etc.
- Autores: Robert C. Martin, Joshua Bloch, Martin Fowler, etc.

**Préstamos:**
- 6 préstamos de ejemplo
- Varios usuarios: juan.perez@example.com, maria.garcia@example.com, etc.
- Diferentes estados: ACTIVE, RETURNED

## ❓ Problemas comunes

**Error: "port is already allocated"**
- Solución: Algún puerto está en uso. Detén otros servicios o cambia los puertos en `docker-compose.yml`

**Los servicios no inician**
- Espera 2-3 minutos para que las bases de datos se inicialicen completamente
- Verifica los logs: `docker-compose logs`

**Error de conexión a base de datos**
- Verifica que PostgreSQL esté saludable: `docker-compose ps`
- Reinicia los contenedores: `docker-compose restart`

## 🎉 ¡Listo!

Tu sistema de microservicios de biblioteca está funcionando con:
- ✅ Programación reactiva (WebFlux + R2DBC)
- ✅ PostgreSQL con datos de ejemplo
- ✅ API Gateway configurado
- ✅ 2 microservicios independientes
- ✅ Docker Compose orquestando todo

**Usa siempre el API Gateway en el puerto 8080 para acceder a los servicios.**

