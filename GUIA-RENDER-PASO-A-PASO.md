# 🚀 Guía Completa: Desplegar en Render Paso a Paso

## 📋 Tabla de Contenido
1. [Crear Cuenta en Render](#paso-1-crear-cuenta-en-render)
2. [Conectar GitHub](#paso-2-conectar-github)
3. [Crear Blueprint](#paso-3-crear-blueprint)
4. [Entender qué está pasando](#paso-4-entender-qué-está-pasando)
5. [Esperar a que se construya todo](#paso-5-esperar-despliegue)
6. [Inicializar las Bases de Datos](#paso-6-inicializar-bases-de-datos)
7. [Probar el Sistema](#paso-7-probar-el-sistema)
8. [Actualizar Postman](#paso-8-actualizar-postman)

---

## PASO 1: Crear Cuenta en Render

### 1.1 Ir a Render
1. Abre tu navegador
2. Ve a: **https://render.com**
3. En la esquina superior derecha, haz click en **"Sign Up"** (Registrarse)

### 1.2 Registrarte
Tienes 3 opciones:

**Opción A - Con GitHub (RECOMENDADO):**
1. Click en **"Sign up with GitHub"**
2. Te redirigirá a GitHub
3. Autoriza a Render
4. ¡Listo! Ya tienes cuenta

**Opción B - Con GitLab:**
1. Click en **"Sign up with GitLab"**
2. Autoriza a Render

**Opción C - Con Email:**
1. Click en **"Sign up with email"**
2. Ingresa tu email
3. Crea una contraseña
4. Verifica tu email

### 1.3 Verificar Email
1. Ve a tu bandeja de entrada
2. Busca email de "Render"
3. Click en el enlace de verificación
4. Ya estás listo para continuar

---

## PASO 2: Conectar GitHub

### 2.1 Acceder al Dashboard
1. Una vez dentro de Render, verás el **Dashboard** (tablero principal)
2. Está vacío porque aún no has creado nada

### 2.2 Conectar GitHub (Si usaste email para registro)
Si te registraste con email:

1. Ve a **Settings** (arriba a la derecha, click en tu avatar)
2. En el menú izquierdo, click en **"Account"**
3. Busca la sección **"Connected Accounts"**
4. Click en **"Connect GitHub"**
5. Autoriza a Render en GitHub
6. Selecciona qué repositorios puede ver Render:
   - **Opción 1**: Solo repositorios seleccionados (elige `biblioteca-microservices`)
   - **Opción 2**: Todos los repositorios
7. Click **"Install & Authorize"**

Si te registraste con GitHub, este paso ya está hecho ✅

---

## PASO 3: Crear Blueprint

### 3.1 Ir a Dashboard
1. Click en el logo de **Render** (arriba a la izquierda)
2. Esto te lleva al Dashboard principal

### 3.2 Crear Nuevo Blueprint
1. Busca el botón azul **"New +"** (esquina superior derecha)
2. Click en **"New +"**
3. Se abrirá un menú desplegable con opciones:
   - Web Service
   - Static Site
   - Private Service
   - Background Worker
   - Cron Job
   - **Blueprint** ← ¡Esta es la que necesitas!
   - PostgreSQL
   - Redis

4. **Click en "Blueprint"**

### 3.3 Conectar Repositorio

Ahora verás una página que dice **"Connect a repository"**

1. En la lista de repositorios, busca: **`biblioteca-microservices`**
   
   💡 **Si NO ves tu repositorio:**
   - Click en **"Configure GitHub App"** (abajo)
   - Te llevará a GitHub
   - Busca "Render" en tus aplicaciones instaladas
   - Agrega acceso al repositorio `biblioteca-microservices`
   - Vuelve a Render y refresca la página

2. Una vez que veas tu repositorio, click en **"Connect"** junto a él

### 3.4 Render Detecta render.yaml

1. Render buscará automáticamente el archivo **`render.yaml`** en tu repositorio
2. Verás un mensaje: **"Blueprint found: render.yaml"**
3. Render mostrará una vista previa de todo lo que va a crear:

```
📊 Blueprint Preview:

Databases:
  ✓ biblioteca-inventory-db (PostgreSQL)
  ✓ biblioteca-loan-db (PostgreSQL)

Services:
  ✓ biblioteca-inventory-service (Web Service)
  ✓ biblioteca-loan-service (Web Service)
  ✓ biblioteca-api-gateway (Web Service)
```

### 3.5 Configurar Service Group (Opcional)

1. Verás un campo **"Service Group Name"**
2. Puedes dejarlo como está o cambiarlo a algo como: **"Biblioteca Sistema"**
3. Esto solo agrupa tus servicios visualmente en el Dashboard

### 3.6 Aplicar Blueprint

1. Revisa que todo esté correcto
2. Click en el botón azul grande: **"Apply"**
3. ¡Render comenzará a crear TODO automáticamente! 🎉

---

## PASO 4: Entender qué está pasando

Después de hacer click en "Apply", Render hará lo siguiente:

### 4.1 Crear Bases de Datos (2-3 minutos)

Render creará 2 bases de datos PostgreSQL:

1. **biblioteca-inventory-db**
   - Base de datos: `inventory_db`
   - Usuario: `inventory_user`
   - Password: generado automáticamente
   - Estado: "Creating..." → "Available"

2. **biblioteca-loan-db**
   - Base de datos: `loan_db`
   - Usuario: `loan_user`
   - Password: generado automáticamente
   - Estado: "Creating..." → "Available"

💡 **Por qué toma tiempo:** Render está provisionando servidores PostgreSQL reales

### 4.2 Construir Imágenes Docker (5-8 minutos)

Para cada servicio, Render:

1. **Clone** tu repositorio de GitHub
2. **Lee** el Dockerfile correspondiente:
   - `inventory-service/Dockerfile`
   - `loan-service/Dockerfile`
   - `api-gateway/Dockerfile`
3. **Ejecuta** Maven para compilar el código Java
4. **Crea** la imagen Docker optimizada
5. **Sube** la imagen a su registro interno

💡 **Por qué toma tiempo:** Está compilando 3 proyectos Spring Boot completos

### 4.3 Desplegar Servicios (2-3 minutos)

Una vez construidas las imágenes, Render:

1. **Crea contenedores** para cada servicio
2. **Inyecta** las variables de entorno automáticamente:
   - Connection strings de bases de datos
   - URLs entre servicios
   - Puertos y configuración
3. **Inicia** los servicios
4. **Ejecuta** health checks para verificar que funcionen
5. **Asigna** URLs públicas

---

## PASO 5: Esperar Despliegue

### 5.1 Ver el Progreso

En el Dashboard verás 5 "cards" (tarjetas):

```
┌─────────────────────────────────────┐
│ 📊 biblioteca-inventory-db          │
│ Type: PostgreSQL                    │
│ Status: 🟡 Creating...              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 📊 biblioteca-loan-db               │
│ Type: PostgreSQL                    │
│ Status: 🟡 Creating...              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 🌐 biblioteca-inventory-service     │
│ Type: Web Service                   │
│ Status: 🟡 Building...              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 🌐 biblioteca-loan-service          │
│ Type: Web Service                   │
│ Status: 🟡 Building...              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 🌐 biblioteca-api-gateway           │
│ Type: Web Service                   │
│ Status: 🟡 Building...              │
└─────────────────────────────────────┘
```

### 5.2 Ver Logs en Tiempo Real

Para ver qué está pasando:

1. **Click en cualquier servicio** (por ejemplo, `biblioteca-inventory-service`)
2. En la página del servicio, verás pestañas:
   - **Events** ← Los pasos que está ejecutando
   - **Logs** ← Salida de consola en tiempo real
   - **Environment**
   - **Settings**
   - etc.

3. Click en **"Logs"** para ver:
```
[Build] Cloning repository...
[Build] Running docker build...
[Build] Step 1/15: FROM maven:3.9.5...
[Build] Step 2/15: WORKDIR /app...
[Build] Downloading Maven dependencies...
[Build] Compiling Java code...
[Build] Running tests...
[Build] Packaging JAR...
[Build] Build complete!
[Deploy] Starting container...
[Deploy] Application started on port 8081
```

### 5.3 Estados Posibles

Cada servicio pasará por estos estados:

1. **🟡 Creating** - Creando infraestructura
2. **🟡 Building** - Construyendo imagen Docker
3. **🟡 Deploying** - Desplegando contenedor
4. **🟢 Live** - ¡Funcionando correctamente!

O en caso de error:
5. **🔴 Build failed** - Error al construir
6. **🔴 Deploy failed** - Error al desplegar

### 5.4 Tiempo Total Estimado

| Componente | Tiempo |
|------------|--------|
| Bases de datos | 2-3 min |
| Inventory Service | 5-8 min |
| Loan Service | 5-8 min |
| API Gateway | 5-8 min |
| **TOTAL** | **10-15 min** |

💡 **Consejo:** Los servicios se construyen en paralelo, así que no es la suma de todos

### 5.5 ¿Cuándo Está Listo?

Está listo cuando veas:
```
✅ biblioteca-inventory-db      🟢 Available
✅ biblioteca-loan-db           🟢 Available
✅ biblioteca-inventory-service 🟢 Live
✅ biblioteca-loan-service      🟢 Live
✅ biblioteca-api-gateway       🟢 Live
```

---

## PASO 6: Inicializar Bases de Datos

⚠️ **MUY IMPORTANTE:** Aunque los servicios estén "Live", las tablas NO existen todavía. Debes crearlas manualmente.

### 6.1 Por Qué Necesitas Hacer Esto

Los servicios Spring Boot están corriendo, pero cuando intentan conectarse a la base de datos, no encuentran las tablas `books` y `loans`, entonces fallan.

Necesitas ejecutar los scripts SQL que creamos:
- `database/init-inventory-db.sql`
- `database/init-loan-db.sql`

### 6.2 Obtener Connection String - Inventory DB

1. En el Dashboard, **click en `biblioteca-inventory-db`**
2. Verás información de la base de datos
3. Busca la sección **"Connections"**
4. Verás varias formas de conectarte:

```
Internal Database URL:
postgresql://inventory_user:abc123xyz@dpg-xxxxx-a.oregon-postgres.render.com/inventory_db

External Database URL:
postgresql://inventory_user:abc123xyz@dpg-xxxxx-a.oregon-postgres.render.com/inventory_db

PSQL Command:
PGPASSWORD=abc123xyz psql -h dpg-xxxxx-a.oregon-postgres.render.com -U inventory_user inventory_db
```

5. **Copia** el "PSQL Command" (el último)

### 6.3 Conectar y Ejecutar Script - Inventory DB

#### Opción A: Desde tu Terminal Local (Recomendado)

**Prerrequisito:** Tener PostgreSQL instalado localmente

**En Windows:**
```powershell
# Si no tienes psql, instala PostgreSQL desde:
# https://www.postgresql.org/download/windows/

# Navega a tu proyecto
cd C:\Users\VALENTINA BARRETO\Downloads\despliegue

# Conectar (reemplaza con tu comando real de Render)
set PGPASSWORD=TU_PASSWORD_AQUI
psql -h dpg-xxxxx-a.oregon-postgres.render.com -U inventory_user -d inventory_db

# Una vez conectado, ejecuta el script:
\i database/init-inventory-db.sql

# Verifica que se creó:
\dt

# Deberías ver:
#          List of relations
#  Schema |  Name  | Type  |     Owner
# --------+--------+-------+---------------
#  public | books  | table | inventory_user

# Ver datos insertados:
SELECT COUNT(*) FROM books;
# Debería mostrar: 10

# Salir:
\q
```

**En Mac/Linux:**
```bash
# Navega a tu proyecto
cd ~/Downloads/despliegue

# Conectar (reemplaza con tu comando real de Render)
export PGPASSWORD='TU_PASSWORD_AQUI'
psql -h dpg-xxxxx-a.oregon-postgres.render.com -U inventory_user -d inventory_db

# Una vez conectado, ejecuta el script:
\i database/init-inventory-db.sql

# Verifica que se creó:
\dt

# Ver datos:
SELECT COUNT(*) FROM books;

# Salir:
\q
```

#### Opción B: Desde Render Shell (Si no tienes psql)

1. En Render Dashboard, ve a **`biblioteca-inventory-db`**
2. En el menú superior, busca **"Shell"** o **"Connect"**
3. Click en **"Connect via PSQL"** (abrirá una terminal web)
4. Copia y pega el contenido completo de `database/init-inventory-db.sql`
5. Presiona Enter
6. Verás:
```
CREATE TABLE
CREATE INDEX
CREATE INDEX
...
INSERT 0 10
```

### 6.4 Ejecutar Script - Loan DB

Repite el mismo proceso para la segunda base de datos:

1. Ve a **`biblioteca-loan-db`** en Dashboard
2. Copia el PSQL Command
3. Conéctate desde tu terminal:

```powershell
# Windows
set PGPASSWORD=TU_PASSWORD_LOAN_DB
psql -h dpg-yyyyy-a.oregon-postgres.render.com -U loan_user -d loan_db

# Ejecutar script
\i database/init-loan-db.sql

# Verificar
\dt
SELECT COUNT(*) FROM loans;
# Debería mostrar: 6

\q
```

### 6.5 Verificar que Todo Funcionó

Para cada base de datos, verifica:

**Inventory DB:**
```sql
-- Debe tener 1 tabla
\dt

-- Debe tener 10 libros
SELECT COUNT(*) FROM books;

-- Ver algunos libros
SELECT title, author FROM books LIMIT 3;
```

**Loan DB:**
```sql
-- Debe tener 1 tabla
\dt

-- Debe tener 6 préstamos
SELECT COUNT(*) FROM loans;

-- Ver algunos préstamos
SELECT user_name, book_id FROM loans LIMIT 3;
```

### 6.6 Reiniciar Servicios (Importante)

Después de crear las tablas:

1. Ve al Dashboard de Render
2. Para cada servicio:
   - Click en **`biblioteca-inventory-service`**
   - En la esquina superior derecha, click en **"Manual Deploy"**
   - Selecciona **"Clear build cache & deploy"**
   - Esto reiniciará el servicio y ahora SÍ encontrará las tablas

3. Repite para:
   - **`biblioteca-loan-service`**
   - **`biblioteca-api-gateway`**

---

## PASO 7: Probar el Sistema

### 7.1 Obtener la URL del API Gateway

1. Ve al Dashboard de Render
2. Click en **`biblioteca-api-gateway`**
3. En la parte superior verás la URL pública:
```
🌐 https://biblioteca-api-gateway.onrender.com
```
4. **Copia esta URL** (la necesitarás)

### 7.2 Probar con curl (Línea de Comandos)

Abre una terminal:

```bash
# Obtener todos los libros
curl https://biblioteca-api-gateway.onrender.com/api/books

# Si devuelve JSON con libros, ¡FUNCIONA! 🎉
```

⚠️ **Primera Petición:** Puede tomar 30-60 segundos porque los servicios se "despiertan" del modo sleep

### 7.3 Probar con el Navegador

1. Abre tu navegador
2. Pega en la barra de direcciones:
```
https://biblioteca-api-gateway.onrender.com/api/books
```
3. Deberías ver un JSON con 10 libros

### 7.4 Probar Health Checks

Verifica que todos los servicios estén saludables:

```bash
# API Gateway
curl https://biblioteca-api-gateway.onrender.com/actuator/health

# Inventory Service (reemplaza con tu URL)
curl https://biblioteca-inventory-service.onrender.com/actuator/health

# Loan Service
curl https://biblioteca-loan-service.onrender.com/actuator/health
```

Todos deben responder:
```json
{"status":"UP"}
```

---

## PASO 8: Actualizar Postman

### 8.1 Crear Environment en Postman

1. Abre **Postman**
2. En la barra lateral izquierda, click en **"Environments"**
3. Click en el botón **"+"** para crear nuevo environment
4. Nombra el environment: **"Render Production"**

### 8.2 Agregar Variables

Agrega la siguiente variable:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| `gateway_url` | `https://biblioteca-api-gateway.onrender.com` | `https://biblioteca-api-gateway.onrender.com` |

⚠️ **Importante:** Reemplaza con tu URL real de Render

### 8.3 Actualizar la Colección

1. Abre tu colección: **"Biblioteca Microservices - API Gateway"**
2. Para cada petición, verifica que use la variable:
```
{{gateway_url}}/api/books
```
3. En la esquina superior derecha de Postman, selecciona el environment: **"Render Production"**

### 8.4 Probar Peticiones

Prueba estas peticiones clave:

1. **GET** Obtener todos los libros
```
{{gateway_url}}/api/books
```

2. **POST** Crear un préstamo
```
{{gateway_url}}/api/loans
Body (JSON):
{
  "bookId": 7,
  "userEmail": "test@render.com",
  "userName": "Usuario Render",
  "dueDate": "2025-11-20",
  "notes": "Prueba desde Render"
}
```

3. **GET** Ver el préstamo creado
```
{{gateway_url}}/api/loans
```

---

## 🎉 ¡SISTEMA DESPLEGADO EXITOSAMENTE!

### URLs de tu Sistema:

```
API Gateway (Punto de entrada):
https://biblioteca-api-gateway.onrender.com

Inventory Service:
https://biblioteca-inventory-service.onrender.com

Loan Service:
https://biblioteca-loan-service.onrender.com

Bases de Datos:
✓ biblioteca-inventory-db (PostgreSQL)
✓ biblioteca-loan-db (PostgreSQL)
```

### ✅ Checklist Final

- [x] Cuenta en Render creada
- [x] GitHub conectado
- [x] Blueprint aplicado
- [x] 5 servicios desplegados
- [x] Tablas creadas en ambas bases de datos
- [x] Servicios reiniciados
- [x] Sistema probado con curl
- [x] Postman actualizado con URLs de producción

---

## 🔄 Actualizar tu Sistema

Cada vez que hagas cambios:

```bash
# 1. Hacer cambios en tu código local
# 2. Commit
git add .
git commit -m "Actualización de funcionalidad"

# 3. Push a GitHub
git push

# 4. Render detectará el push automáticamente y redesplegará
```

¡No necesitas hacer nada más! Render hace auto-deploy.

---

## 🆘 Solución de Problemas

### "Service is failing health checks"

**Causa:** Las tablas no existen o el servicio no puede conectarse a la BD

**Solución:**
1. Verifica que ejecutaste los scripts SQL
2. Reinicia el servicio en Render
3. Revisa los logs del servicio

### "502 Bad Gateway"

**Causa:** El servicio está despertando del modo sleep (plan gratuito)

**Solución:**
1. Espera 30-60 segundos
2. Intenta de nuevo
3. Es normal en la primera petición

### "Connection timeout"

**Causa:** Loan Service no puede comunicarse con Inventory Service

**Solución:**
1. Verifica que ambos servicios estén "Live" (🟢)
2. Ve a `biblioteca-loan-service` → Environment
3. Verifica que `INVENTORY_SERVICE_URL` esté correcta

### Los logs muestran "Table 'books' doesn't exist"

**Causa:** No ejecutaste los scripts SQL

**Solución:**
1. Conéctate a las bases de datos
2. Ejecuta los scripts
3. Reinicia los servicios

---

## 📞 Soporte

Si tienes problemas:
1. Revisa los **Logs** del servicio en Render
2. Consulta la [Documentación de Render](https://render.com/docs)
3. Revisa este documento nuevamente

---

¡Felicidades! Tu sistema de microservicios está ahora en producción en la nube con:
✅ HTTPS automático
✅ Auto-deploy desde GitHub
✅ Bases de datos PostgreSQL gestionadas
✅ Health monitoring
✅ Escalabilidad automática

🎊 **¡Disfruta tu sistema en la nube!** 🎊

