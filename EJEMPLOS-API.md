# 📡 Ejemplos Completos de API

Todos los ejemplos usan el **API Gateway en el puerto 8080**.

## 📚 INVENTORY SERVICE - Gestión de Libros

### 1. Obtener todos los libros
```bash
curl http://localhost:8080/api/books
```

### 2. Obtener libro por ID
```bash
curl http://localhost:8080/api/books/1
```

### 3. Obtener libro por ISBN
```bash
curl http://localhost:8080/api/books/isbn/978-0-13-468599-1
```

### 4. Buscar libros por autor
```bash
curl "http://localhost:8080/api/books/search/author?name=Martin"
```

### 5. Buscar libros por título
```bash
curl "http://localhost:8080/api/books/search/title?name=Spring"
```

### 6. Obtener libros por categoría
```bash
curl http://localhost:8080/api/books/category/Programación
```

### 7. Obtener libros disponibles
```bash
curl http://localhost:8080/api/books/available
```

### 8. Verificar disponibilidad de un libro
```bash
curl http://localhost:8080/api/books/1/availability
```

### 9. Crear un nuevo libro
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "978-1-234-56789-0",
    "title": "Programación Reactiva con Spring",
    "author": "Juan Desarrollador",
    "publisher": "Tech Books",
    "publicationYear": 2024,
    "category": "Programación",
    "totalCopies": 5,
    "availableCopies": 5,
    "description": "Guía completa de programación reactiva"
  }'
```

### 10. Actualizar un libro
```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "978-0-13-468599-1",
    "title": "Effective Java - Edición Actualizada",
    "author": "Joshua Bloch",
    "publisher": "Addison-Wesley",
    "publicationYear": 2018,
    "category": "Programación",
    "totalCopies": 6,
    "availableCopies": 6,
    "description": "Guía completa de mejores prácticas en Java - Nueva edición"
  }'
```

### 11. Eliminar un libro
```bash
curl -X DELETE http://localhost:8080/api/books/11
```

### 12. Decrementar copias disponibles (para préstamo interno)
```bash
curl -X POST http://localhost:8080/api/books/1/decrement
```

### 13. Incrementar copias disponibles (para devolución interna)
```bash
curl -X POST http://localhost:8080/api/books/1/increment
```

---

## 📖 LOAN SERVICE - Gestión de Préstamos

### 1. Obtener todos los préstamos
```bash
curl http://localhost:8080/api/loans
```

### 2. Obtener préstamo por ID
```bash
curl http://localhost:8080/api/loans/1
```

### 3. Obtener préstamos por usuario
```bash
curl http://localhost:8080/api/loans/user/juan.perez@example.com
```

### 4. Obtener préstamos activos de un usuario
```bash
curl http://localhost:8080/api/loans/user/juan.perez@example.com/active
```

### 5. Obtener préstamos por libro
```bash
curl http://localhost:8080/api/loans/book/1
```

### 6. Obtener préstamos por estado
```bash
# Estados válidos: ACTIVE, RETURNED, OVERDUE, CANCELLED
curl http://localhost:8080/api/loans/status/ACTIVE
```

### 7. Obtener préstamos vencidos
```bash
curl http://localhost:8080/api/loans/overdue
```

### 8. Crear un nuevo préstamo
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 7,
    "userEmail": "lucia.rodriguez@example.com",
    "userName": "Lucía Rodríguez",
    "dueDate": "2025-11-18",
    "notes": "Préstamo para proyecto de investigación"
  }'
```

**Nota:** Al crear un préstamo:
- ✅ Se verifica la disponibilidad del libro
- ✅ Se verifica que el usuario no exceda el límite de 5 préstamos activos
- ✅ Se decrementa automáticamente el contador de copias disponibles

### 9. Devolver un libro
```bash
curl -X POST http://localhost:8080/api/loans/1/return
```

**Nota:** Al devolver:
- ✅ Se marca la fecha de devolución
- ✅ Se cambia el estado a RETURNED
- ✅ Se incrementa automáticamente el contador de copias disponibles

### 10. Renovar un préstamo (extender fecha)
```bash
curl -X POST "http://localhost:8080/api/loans/2/renew?newDueDate=2025-11-25"
```

### 11. Cancelar un préstamo
```bash
curl -X POST http://localhost:8080/api/loans/4/cancel
```

**Nota:** Al cancelar:
- ✅ Se cambia el estado a CANCELLED
- ✅ Se incrementa el contador de copias disponibles

### 12. Actualizar préstamos vencidos (tarea administrativa)
```bash
curl -X POST http://localhost:8080/api/loans/update-overdue
```

---

## 🏥 HEALTH CHECKS

### API Gateway
```bash
curl http://localhost:8080/actuator/health
```

### Inventory Service (directo)
```bash
curl http://localhost:8081/actuator/health
```

### Loan Service (directo)
```bash
curl http://localhost:8082/actuator/health
```

---

## 🔄 Flujo Completo de Ejemplo

### Escenario: Usuario solicita préstamo de un libro

**Paso 1: Verificar que el libro existe y está disponible**
```bash
curl http://localhost:8080/api/books/7
```

**Paso 2: Ver libros disponibles**
```bash
curl http://localhost:8080/api/books/available
```

**Paso 3: Crear el préstamo**
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 7,
    "userEmail": "nuevo.usuario@example.com",
    "userName": "Nuevo Usuario",
    "dueDate": "2025-11-20",
    "notes": "Primer préstamo"
  }'
```

**Paso 4: Verificar que el libro ahora tiene menos copias disponibles**
```bash
curl http://localhost:8080/api/books/7
```

**Paso 5: Ver préstamos activos del usuario**
```bash
curl http://localhost:8080/api/loans/user/nuevo.usuario@example.com/active
```

**Paso 6: Devolver el libro (usando el ID del préstamo creado)**
```bash
curl -X POST http://localhost:8080/api/loans/[ID_DEL_PRESTAMO]/return
```

**Paso 7: Verificar que las copias disponibles aumentaron**
```bash
curl http://localhost:8080/api/books/7
```

---

## 📊 Respuestas de Ejemplo

### Libro (BookResponse)
```json
{
  "id": 1,
  "isbn": "978-0-13-468599-1",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "publisher": "Addison-Wesley",
  "publicationYear": 2018,
  "category": "Programación",
  "totalCopies": 5,
  "availableCopies": 4,
  "description": "Guía completa de mejores prácticas en Java",
  "isAvailable": true
}
```

### Préstamo (LoanResponse)
```json
{
  "id": 1,
  "bookId": 7,
  "userEmail": "juan.perez@example.com",
  "userName": "Juan Pérez",
  "loanDate": "2025-10-30",
  "dueDate": "2025-11-13",
  "returnDate": null,
  "status": "ACTIVE",
  "notes": "Primer préstamo del usuario",
  "isOverdue": false
}
```

---

## 🎯 Datos de Prueba Disponibles

### Libros (IDs 1-10)
- ID 1: Effective Java
- ID 2: Clean Code
- ID 3: The Clean Coder
- ID 4: Design Patterns
- ID 5: Refactoring
- ID 6: Spring in Action
- ID 7: Spring Boot in Action
- ID 8: Building Microservices
- ID 9: Domain-Driven Design
- ID 10: Reactive Spring

### Usuarios de Ejemplo
- juan.perez@example.com
- maria.garcia@example.com
- carlos.lopez@example.com
- ana.martinez@example.com
- pedro.sanchez@example.com

---

## 💡 Tips

1. **Usa el API Gateway (puerto 8080)** para todas las peticiones
2. **Los préstamos se crean con dueDate futuro** (fecha de vencimiento)
3. **Límite de 5 préstamos activos** por usuario
4. **Las copias se gestionan automáticamente** al crear/devolver préstamos
5. **Los estados de préstamo son:** ACTIVE, RETURNED, OVERDUE, CANCELLED

