# 🐳 Comandos Útiles de Docker

## Comandos Básicos

### Iniciar el sistema completo
```bash
docker-compose up --build
```

### Iniciar en modo background (segundo plano)
```bash
docker-compose up -d --build
```

### Detener todos los servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (resetear base de datos)
```bash
docker-compose down -v
```

---

## Ver Logs

### Ver logs de todos los servicios
```bash
docker-compose logs
```

### Ver logs en tiempo real (seguir)
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico
```bash
docker-compose logs inventory-service
docker-compose logs loan-service
docker-compose logs api-gateway
docker-compose logs postgres-inventory
docker-compose logs postgres-loan
```

### Ver logs en tiempo real de un servicio
```bash
docker-compose logs -f inventory-service
```

### Ver últimas 100 líneas de logs
```bash
docker-compose logs --tail=100
```

---

## Estado de los Servicios

### Ver estado de todos los contenedores
```bash
docker-compose ps
```

### Ver información detallada de un servicio
```bash
docker-compose ps api-gateway
```

### Ver estadísticas de recursos (CPU, RAM, etc)
```bash
docker stats
```

---

## Reiniciar Servicios

### Reiniciar todos los servicios
```bash
docker-compose restart
```

### Reiniciar un servicio específico
```bash
docker-compose restart inventory-service
docker-compose restart loan-service
docker-compose restart api-gateway
```

---

## Acceder a los Contenedores

### Abrir shell en un contenedor
```bash
# PostgreSQL Inventory
docker exec -it postgres-inventory psql -U inventory_user -d inventory_db

# PostgreSQL Loan
docker exec -it postgres-loan psql -U loan_user -d loan_db
```

### Comandos SQL útiles dentro de PostgreSQL

#### En inventory_db:
```sql
-- Ver todos los libros
SELECT * FROM books;

-- Ver libros disponibles
SELECT title, author, available_copies FROM books WHERE available_copies > 0;

-- Contar libros por categoría
SELECT category, COUNT(*) FROM books GROUP BY category;
```

#### En loan_db:
```sql
-- Ver todos los préstamos
SELECT * FROM loans;

-- Ver préstamos activos
SELECT * FROM loans WHERE status = 'ACTIVE';

-- Ver préstamos vencidos
SELECT * FROM loans WHERE status = 'ACTIVE' AND due_date < CURRENT_DATE;

-- Préstamos por usuario
SELECT user_name, COUNT(*) FROM loans GROUP BY user_name;
```

---

## Construcción y Limpieza

### Reconstruir las imágenes sin caché
```bash
docker-compose build --no-cache
```

### Eliminar imágenes no utilizadas
```bash
docker image prune
```

### Eliminar todo (contenedores, redes, volúmenes, imágenes)
```bash
docker system prune -a --volumes
```

⚠️ **CUIDADO**: Este comando eliminará TODO, incluyendo otros proyectos Docker

---

## Verificar Conectividad

### Verificar que los servicios estén saludables
```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Inventory Service
curl http://localhost:8081/actuator/health

# Loan Service
curl http://localhost:8082/actuator/health
```

### Probar conectividad entre servicios
```bash
# Desde el host, probar el API Gateway
curl http://localhost:8080/api/books

# Ver si el gateway puede comunicarse con inventory service
docker exec -it api-gateway wget -qO- http://inventory-service:8081/actuator/health
```

---

## Gestión de Volúmenes

### Listar volúmenes
```bash
docker volume ls
```

### Ver detalles de un volumen
```bash
docker volume inspect despliegue_postgres-inventory-data
docker volume inspect despliegue_postgres-loan-data
```

### Eliminar volúmenes no utilizados
```bash
docker volume prune
```

---

## Troubleshooting

### Ver por qué un contenedor falló
```bash
docker-compose logs inventory-service | tail -100
```

### Inspeccionar un contenedor
```bash
docker inspect inventory-service
```

### Ver procesos dentro de un contenedor
```bash
docker top inventory-service
```

### Verificar el estado de salud
```bash
docker inspect --format='{{.State.Health.Status}}' inventory-service
```

### Forzar recreación de contenedores
```bash
docker-compose up --force-recreate
```

---

## Comandos de Desarrollo

### Compilar solo un servicio
```bash
docker-compose build inventory-service
```

### Iniciar solo un servicio y sus dependencias
```bash
docker-compose up inventory-service
```

### Escalar un servicio (crear múltiples instancias)
```bash
docker-compose up --scale inventory-service=3
```

### Ver red de Docker
```bash
docker network ls
docker network inspect despliegue_biblioteca-network
```

---

## Backups

### Hacer backup de la base de datos de inventario
```bash
docker exec postgres-inventory pg_dump -U inventory_user inventory_db > backup-inventory.sql
```

### Hacer backup de la base de datos de préstamos
```bash
docker exec postgres-loan pg_dump -U loan_user loan_db > backup-loan.sql
```

### Restaurar backup
```bash
docker exec -i postgres-inventory psql -U inventory_user -d inventory_db < backup-inventory.sql
```

---

## Scripts Rápidos

### Script para verificar todo
```bash
#!/bin/bash
echo "=== Estado de contenedores ==="
docker-compose ps

echo -e "\n=== Health checks ==="
echo "API Gateway:"
curl -s http://localhost:8080/actuator/health | python -m json.tool

echo -e "\nInventory Service:"
curl -s http://localhost:8081/actuator/health | python -m json.tool

echo -e "\nLoan Service:"
curl -s http://localhost:8082/actuator/health | python -m json.tool
```

### Script para ver logs de todos los servicios
```bash
#!/bin/bash
echo "=== API Gateway ==="
docker-compose logs --tail=20 api-gateway

echo -e "\n=== Inventory Service ==="
docker-compose logs --tail=20 inventory-service

echo -e "\n=== Loan Service ==="
docker-compose logs --tail=20 loan-service
```

---

## Monitoreo Continuo

### Ver logs de todos los servicios en tiempo real
```bash
docker-compose logs -f --tail=100
```

### Monitorear recursos en tiempo real
```bash
watch docker stats
```

---

## Comandos de Emergencia

### Detener todo inmediatamente
```bash
docker-compose kill
```

### Eliminar todo y empezar de cero
```bash
docker-compose down -v
docker-compose up --build
```

### Ver último error de un servicio
```bash
docker-compose logs inventory-service | grep ERROR | tail -20
```

---

## 💡 Tips

1. Usa `-d` (detached) para correr en background: `docker-compose up -d`
2. Usa `-f` (follow) para seguir los logs en tiempo real: `docker-compose logs -f`
3. Usa `--tail=N` para ver solo las últimas N líneas de logs
4. Combina comandos: `docker-compose logs -f --tail=50 inventory-service`
5. Usa `docker-compose ps` frecuentemente para verificar el estado

