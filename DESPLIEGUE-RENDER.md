# 🚀 Guía de Despliegue en Render

Esta guía te llevará paso a paso para desplegar el sistema completo de microservicios en Render.

## 📋 Prerrequisitos

1. **Cuenta en Render**: Crea una cuenta gratuita en [render.com](https://render.com)
2. **Repositorio Git**: Sube tu proyecto a GitHub, GitLab o Bitbucket
3. **Cuenta verificada**: Verifica tu email en Render

## 🎯 Opción 1: Despliegue Automático con render.yaml (Recomendado)

### Paso 1: Subir código a Git

```bash
# Inicializar repositorio (si no lo has hecho)
git init

# Agregar archivos
git add .

# Hacer commit
git commit -m "Sistema de biblioteca - Microservicios"

# Agregar remote (reemplaza con tu URL)
git remote add origin https://github.com/TU_USUARIO/biblioteca-microservices.git

# Push
git push -u origin main
```

### Paso 2: Conectar Render con tu repositorio

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Click en **"New +"** → **"Blueprint"**
3. Selecciona **"Connect a repository"**
4. Autoriza acceso a tu GitHub/GitLab
5. Selecciona el repositorio `biblioteca-microservices`
6. Render detectará automáticamente el archivo `render.yaml`

### Paso 3: Configurar y Desplegar

1. Render mostrará todos los servicios definidos en `render.yaml`:
   - ✅ biblioteca-inventory-db (PostgreSQL)
   - ✅ biblioteca-loan-db (PostgreSQL)
   - ✅ biblioteca-inventory-service
   - ✅ biblioteca-loan-service
   - ✅ biblioteca-api-gateway

2. Click en **"Apply"**
3. Render comenzará a:
   - Crear las bases de datos PostgreSQL
   - Construir las imágenes Docker
   - Desplegar los servicios
   - Configurar las URLs automáticamente

### Paso 4: Inicializar las Bases de Datos

Una vez desplegados los servicios, debes ejecutar los scripts SQL manualmente:

#### Para Inventory Database:

1. En Render Dashboard, ve a **biblioteca-inventory-db**
2. Click en **"Connect"** → Copia el comando de conexión
3. En tu terminal local:

```bash
# Conectar a la base de datos (reemplaza con tu connection string)
psql postgresql://inventory_user:PASSWORD@HOST/inventory_db

# Ejecutar el script
\i database/init-inventory-db.sql

# Salir
\q
```

#### Para Loan Database:

```bash
# Conectar a la base de datos
psql postgresql://loan_user:PASSWORD@HOST/loan_db

# Ejecutar el script
\i database/init-loan-db.sql

# Salir
\q
```

---

## 🎯 Opción 2: Despliegue Manual (Paso a Paso)

### Paso 1: Crear Bases de Datos PostgreSQL

#### Base de datos para Inventory Service:
1. En Render Dashboard → **"New +"** → **"PostgreSQL"**
2. Configurar:
   - **Name**: `biblioteca-inventory-db`
   - **Database**: `inventory_db`
   - **User**: `inventory_user`
   - **Region**: Oregon (o el más cercano)
   - **Plan**: Free
3. Click **"Create Database"**
4. Espera a que se cree (toma ~2 minutos)
5. Una vez creada, conéctate y ejecuta el script `database/init-inventory-db.sql`

#### Base de datos para Loan Service:
Repite el proceso con:
- **Name**: `biblioteca-loan-db`
- **Database**: `loan_db`
- **User**: `loan_user`

### Paso 2: Desplegar Inventory Service

1. **"New +"** → **"Web Service"**
2. Conectar tu repositorio
3. Configurar:
   - **Name**: `biblioteca-inventory-service`
   - **Runtime**: Docker
   - **Region**: Oregon
   - **Branch**: main
   - **Dockerfile Path**: `./inventory-service/Dockerfile`
   - **Docker Context**: `.`
   - **Plan**: Free

4. **Variables de Entorno**:
   ```
   SPRING_PROFILES_ACTIVE=render
   SERVER_PORT=8081
   SPRING_R2DBC_URL=[Internal Connection String de biblioteca-inventory-db en formato r2dbc]
   SPRING_R2DBC_USERNAME=inventory_user
   SPRING_R2DBC_PASSWORD=[password de la BD]
   ```

5. **Health Check Path**: `/actuator/health`
6. Click **"Create Web Service"**

### Paso 3: Desplegar Loan Service

1. **"New +"** → **"Web Service"**
2. Configurar igual que Inventory Service pero con:
   - **Name**: `biblioteca-loan-service`
   - **Dockerfile Path**: `./loan-service/Dockerfile`
   - **Variables de Entorno**:
     ```
     SPRING_PROFILES_ACTIVE=render
     SERVER_PORT=8082
     SPRING_R2DBC_URL=[Connection String de biblioteca-loan-db]
     SPRING_R2DBC_USERNAME=loan_user
     SPRING_R2DBC_PASSWORD=[password de la BD]
     INVENTORY_SERVICE_URL=https://biblioteca-inventory-service.onrender.com
     ```

### Paso 4: Desplegar API Gateway

1. **"New +"** → **"Web Service"**
2. Configurar:
   - **Name**: `biblioteca-api-gateway`
   - **Dockerfile Path**: `./api-gateway/Dockerfile`
   - **Variables de Entorno**:
     ```
     SPRING_PROFILES_ACTIVE=render
     SERVER_PORT=8080
     INVENTORY_SERVICE_URL=https://biblioteca-inventory-service.onrender.com
     LOAN_SERVICE_URL=https://biblioteca-loan-service.onrender.com
     ```

---

## 🔄 Convertir Connection String de PostgreSQL a R2DBC

Render te da una URL como:
```
postgresql://user:pass@host:5432/dbname
```

Debes convertirla a formato R2DBC:
```
r2dbc:postgresql://host:5432/dbname
```

Ejemplo:
```bash
# Render te da:
postgresql://inventory_user:abc123@dpg-xyz.oregon-postgres.render.com:5432/inventory_db

# Tú usas:
r2dbc:postgresql://dpg-xyz.oregon-postgres.render.com:5432/inventory_db
```

---

## 🌐 URLs de tu Sistema Desplegado

Una vez completado el despliegue, tendrás URLs como:

```
API Gateway:
https://biblioteca-api-gateway.onrender.com

Inventory Service:
https://biblioteca-inventory-service.onrender.com

Loan Service:
https://biblioteca-loan-service.onrender.com
```

### Probar el sistema:

```bash
# Ver todos los libros
curl https://biblioteca-api-gateway.onrender.com/api/books

# Ver health check
curl https://biblioteca-api-gateway.onrender.com/actuator/health
```

---

## ⚠️ Limitaciones del Plan Gratuito de Render

1. **Sleep automático**: Los servicios se duermen después de 15 minutos de inactividad
   - Primera petición puede tardar 30-60 segundos
   
2. **Límites mensuales**:
   - 750 horas de runtime por mes
   - 100 GB de ancho de banda
   
3. **PostgreSQL Free**:
   - Expira después de 90 días
   - 1 GB de almacenamiento
   - Se elimina si no hay actividad por 90 días

---

## 🔧 Solución de Problemas

### Error: "Service failed to build"
- Verifica que el `Dockerfile Path` sea correcto
- Asegúrate de que `Docker Context` sea `.` (raíz del proyecto)

### Error: "Connection refused" entre servicios
- Usa las URLs completas de Render: `https://nombre-servicio.onrender.com`
- No uses `localhost` o nombres de contenedor en producción

### Error: "Database connection failed"
- Verifica que la connection string esté en formato R2DBC
- Verifica usuario y password
- Asegúrate de que la base de datos esté en estado "Available"

### Servicio muy lento al iniciar
- Es normal en el plan gratuito (sleep automático)
- Primera petición despierta el servicio (30-60 segundos)
- Considera usar un ping periódico o upgrade al plan pagado

---

## 📊 Configurar Variables de Entorno adicionales

En el Dashboard de cada servicio:

1. Ve a **"Environment"**
2. Click **"Add Environment Variable"**
3. Agregar según necesites:
   - `JAVA_OPTS=-Xmx512m` (limitar memoria)
   - `TZ=America/Bogota` (zona horaria)

---

## 🔄 Actualizar el Sistema

### Automático (con render.yaml):
```bash
# Hacer cambios en el código
git add .
git commit -m "Actualización"
git push

# Render detectará el push y redesplegará automáticamente
```

### Manual:
1. Ve al servicio en Render Dashboard
2. Click **"Manual Deploy"** → **"Deploy latest commit"**

---

## 📝 Checklist de Despliegue

- [ ] Código subido a Git
- [ ] Bases de datos PostgreSQL creadas
- [ ] Scripts SQL ejecutados en ambas bases de datos
- [ ] Inventory Service desplegado y funcionando
- [ ] Loan Service desplegado y funcionando
- [ ] API Gateway desplegado y funcionando
- [ ] Variables de entorno configuradas correctamente
- [ ] Health checks respondiendo OK
- [ ] Probado endpoints principales
- [ ] Colección de Postman actualizada con URLs de producción

---

## 🎉 ¡Sistema Desplegado!

Tu sistema de microservicios está ahora en la nube con:
- ✅ Programación reactiva
- ✅ PostgreSQL gestionado
- ✅ Auto-scaling
- ✅ HTTPS automático
- ✅ Health monitoring
- ✅ Logs centralizados

**URL Principal**: `https://biblioteca-api-gateway.onrender.com`

---

## 💰 Costos

- **Plan Gratuito**: $0/mes
  - 3 servicios web
  - 2 bases de datos PostgreSQL
  - Perfecto para desarrollo y demos

- **Plan Starter**: $7/servicio/mes
  - Sin sleep automático
  - Más recursos
  - Mejor rendimiento

---

## 📚 Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Render con Docker](https://render.com/docs/docker)
- [PostgreSQL en Render](https://render.com/docs/databases)
- [Render Blueprint (render.yaml)](https://render.com/docs/blueprint-spec)

