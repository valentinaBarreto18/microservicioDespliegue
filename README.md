# Sistema de Microservicios para Biblioteca 📚

Sistema completo de microservicios desarrollado con **Spring Boot** y **programación reactiva** (WebFlux + R2DBC) para la gestión de inventario de libros y préstamos de biblioteca, utilizando **PostgreSQL** como base de datos.

## 🏗️ Arquitectura del Sistema

El proyecto está compuesto por:

- **API Gateway** (Puerto 8080): Punto de entrada único que enruta las peticiones a los microservicios
- **Inventory Service** (Puerto 8081): Gestión del inventario de libros
- **Loan Service** (Puerto 8082): Gestión de préstamos de libros
- **PostgreSQL**: Dos instancias separadas (una por servicio)

## 📂 Estructura del Proyecto

```
biblioteca-microservices/
├── api-gateway/                    # API Gateway
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/biblioteca/gateway/
│   │       │       ├── ApiGatewayApplication.java
│   │       │       └── controller/
│   │       │           └── FallbackController.java
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── inventory-service/              # Servicio de Inventario
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/biblioteca/inventory/
│   │       │       ├── InventoryServiceApplication.java
│   │       │       ├── model/
│   │       │       │   └── Book.java (Record)
│   │       │       ├── dto/
│   │       │       │   ├── BookRequest.java (Record)
│   │       │       │   └── BookResponse.java (Record)
│   │       │       ├── repository/
│   │       │       │   └── BookRepository.java
│   │       │       ├── service/
│   │       │       │   └── BookService.java
│   │       │       └── controller/
│   │       │           └── BookController.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── schema.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── loan-service/                   # Servicio de Préstamos
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/biblioteca/loan/
│   │       │       ├── LoanServiceApplication.java
│   │       │       ├── model/
│   │       │       │   ├── Loan.java (Record)
│   │       │       │   └── LoanStatus.java (Enum)
│   │       │       ├── dto/
│   │       │       │   ├── LoanRequest.java (Record)
│   │       │       │   └── LoanResponse.java (Record)
│   │       │       ├── repository/
│   │       │       │   └── LoanRepository.java
│   │       │       ├── service/
│   │       │       │   └── LoanService.java
│   │       │       ├── controller/
│   │       │       │   └── LoanController.java
│   │       │       └── config/
│   │       │           └── WebClientConfig.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── schema.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── database/                       # Scripts de Base de Datos
│   ├── init-inventory-db.sql
│   └── init-loan-db.sql
│
├── postman/                        # Colección de Postman
│   └── Biblioteca-Microservices.postman_collection.json
│
├── docker-compose.yml              # Orquestación de contenedores
├── .dockerignore
├── pom.xml                         # POM parent
└── README.md
```

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Programación Reactiva)
- **Spring Cloud Gateway**
- **R2DBC PostgreSQL** (Base de datos reactiva)
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**
- **Java Records** (para DTOs y modelos inmutables)

## 🗄️ Base de Datos PostgreSQL

### Inventory Database (inventory_db)

**Tabla: books**
- `id` (BIGSERIAL PRIMARY KEY)
- `isbn` (VARCHAR - UNIQUE)
- `title` (VARCHAR)
- `author` (VARCHAR)
- `publisher` (VARCHAR)
- `publication_year` (INTEGER)
- `category` (VARCHAR)
- `total_copies` (INTEGER)
- `available_copies` (INTEGER)
- `description` (TEXT)
- `created_at`, `updated_at` (TIMESTAMP)

### Loan Database (loan_db)

**Tabla: loans**
- `id` (BIGSERIAL PRIMARY KEY)
- `book_id` (BIGINT - referencia a libro)
- `user_email` (VARCHAR)
- `user_name` (VARCHAR)
- `loan_date` (DATE)
- `due_date` (DATE)
- `return_date` (DATE - nullable)
- `status` (ENUM: ACTIVE, RETURNED, OVERDUE, CANCELLED)
- `notes` (TEXT)
- `created_at`, `updated_at` (TIMESTAMP)

## 🚀 Cómo Ejecutar el Proyecto

### Opción A: Local con Docker (Desarrollo)

#### Prerrequisitos

- Docker Desktop instalado
- Docker Compose instalado
- Puerto 8080, 8081, 8082, 5432 y 5433 disponibles

### Pasos para ejecutar

1. **Clonar o descargar el proyecto**

2. **Construir y ejecutar con Docker Compose**

```bash
docker-compose up --build
```

Este comando:
- Construye las imágenes Docker de los 3 servicios
- Crea las bases de datos PostgreSQL
- Ejecuta los scripts de inicialización
- Inicia todos los servicios en el orden correcto

3. **Verificar que los servicios estén funcionando**

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Inventory Service
curl http://localhost:8081/actuator/health

# Loan Service
curl http://localhost:8082/actuator/health
```

4. **Acceder a la aplicación**

El API Gateway estará disponible en: `http://localhost:8080`

### Detener los servicios

```bash
docker-compose down
```

### Eliminar volúmenes (resetear bases de datos)

```bash
docker-compose down -v
```

---

### Opción B: Desplegar en la Nube (Render)

Para desplegar en producción en Render (gratis):

```bash
# 1. Subir a GitHub
git init
git add .
git commit -m "Sistema biblioteca"
git push

# 2. Ir a render.com y crear Blueprint
# 3. Conectar repositorio
# 4. Render desplegará automáticamente usando render.yaml
```

**📖 Ver guía completa**: `DESPLIEGUE-RENDER.md` y `RENDER-QUICKSTART.md`

**Tiempo estimado**: ~10 minutos

**Costo**: $0 (plan gratuito)

## 📡 API Endpoints

### Inventory Service (a través del API Gateway)

**Libros**
- `GET /api/books` - Obtener todos los libros
- `GET /api/books/{id}` - Obtener libro por ID
- `GET /api/books/isbn/{isbn}` - Obtener libro por ISBN
- `GET /api/books/search/author?name=autor` - Buscar por autor
- `GET /api/books/search/title?name=titulo` - Buscar por título
- `GET /api/books/category/{category}` - Obtener por categoría
- `GET /api/books/available` - Obtener libros disponibles
- `GET /api/books/{id}/availability` - Verificar disponibilidad
- `POST /api/books` - Crear nuevo libro
- `PUT /api/books/{id}` - Actualizar libro
- `DELETE /api/books/{id}` - Eliminar libro
- `POST /api/books/{id}/decrement` - Decrementar copias
- `POST /api/books/{id}/increment` - Incrementar copias

### Loan Service (a través del API Gateway)

**Préstamos**
- `GET /api/loans` - Obtener todos los préstamos
- `GET /api/loans/{id}` - Obtener préstamo por ID
- `GET /api/loans/user/{email}` - Obtener préstamos por usuario
- `GET /api/loans/user/{email}/active` - Préstamos activos por usuario
- `GET /api/loans/book/{bookId}` - Préstamos por libro
- `GET /api/loans/status/{status}` - Préstamos por estado
- `GET /api/loans/overdue` - Obtener préstamos vencidos
- `POST /api/loans` - Crear nuevo préstamo
- `POST /api/loans/{id}/return` - Devolver libro
- `POST /api/loans/{id}/renew?newDueDate=2025-11-25` - Renovar préstamo
- `POST /api/loans/{id}/cancel` - Cancelar préstamo
- `POST /api/loans/update-overdue` - Actualizar vencidos

## 📮 Colección de Postman

Importa el archivo `postman/Biblioteca-Microservices.postman_collection.json` en Postman para probar todos los endpoints.

La colección incluye:
- ✅ Todas las operaciones CRUD de libros
- ✅ Todas las operaciones de préstamos
- ✅ Búsquedas y filtros
- ✅ Health checks de todos los servicios

## 🔍 Ejemplos de Uso

### Crear un libro

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "978-0-13-468599-9",
    "title": "Test Driven Development",
    "author": "Kent Beck",
    "publisher": "Addison-Wesley",
    "publicationYear": 2002,
    "category": "Programación",
    "totalCopies": 3,
    "availableCopies": 3,
    "description": "Desarrollo guiado por pruebas"
  }'
```

### Crear un préstamo

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 7,
    "userEmail": "lucia.rodriguez@example.com",
    "userName": "Lucía Rodríguez",
    "dueDate": "2025-11-18",
    "notes": "Préstamo de prueba"
  }'
```

### Devolver un libro

```bash
curl -X POST http://localhost:8080/api/loans/1/return
```

## 📊 Características Principales

### Programación Reactiva
- ✅ Uso de Project Reactor (Mono y Flux)
- ✅ R2DBC para acceso reactivo a PostgreSQL
- ✅ WebFlux para endpoints no bloqueantes
- ✅ WebClient para comunicación entre servicios

### Uso de Records
- ✅ DTOs implementados con Java Records
- ✅ Modelos de dominio inmutables
- ✅ Código limpio y conciso

### Microservicios
- ✅ Separación de responsabilidades
- ✅ Bases de datos independientes por servicio
- ✅ Comunicación reactiva entre servicios
- ✅ Circuit Breaker en API Gateway

### Docker
- ✅ Dockerfile multi-stage para optimizar imágenes
- ✅ Docker Compose para orquestación
- ✅ Health checks configurados
- ✅ Redes y volúmenes persistentes

## 🔧 Configuración de Puertos

| Servicio | Puerto |
|----------|--------|
| API Gateway | 8080 |
| Inventory Service | 8081 |
| Loan Service | 8082 |
| PostgreSQL Inventory | 5432 |
| PostgreSQL Loan | 5433 |

## 📝 Notas Importantes

1. Los servicios están configurados con health checks que verifican su disponibilidad
2. El API Gateway espera a que los servicios estén saludables antes de iniciar
3. Las bases de datos se inicializan automáticamente con datos de ejemplo
4. La comunicación entre microservicios es reactiva usando WebClient
5. Se aplican validaciones en los DTOs usando Bean Validation

## 🐛 Troubleshooting

**Los servicios no inician:**
- Verifica que los puertos no estén en uso
- Asegúrate de tener suficiente memoria en Docker

**Error de conexión a base de datos:**
- Espera a que las bases de datos estén completamente iniciadas
- Verifica los logs: `docker-compose logs postgres-inventory`

**Ver logs de un servicio específico:**
```bash
docker-compose logs -f inventory-service
docker-compose logs -f loan-service
docker-compose logs -f api-gateway
```

## 👨‍💻 Autor

Sistema desarrollado con Spring Boot, Programación Reactiva y PostgreSQL.

---

¡Disfruta del sistema de gestión de biblioteca! 📚✨

