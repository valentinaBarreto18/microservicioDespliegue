# 🚀 Inicio Rápido - Despliegue en Render

## Resumen en 5 Pasos

### 1️⃣ Subir a GitHub

```bash
git init
git add .
git commit -m "Sistema biblioteca microservicios"
git remote add origin https://github.com/TU_USUARIO/biblioteca-microservices.git
git push -u origin main
```

### 2️⃣ Crear Cuenta en Render

- Ve a [render.com](https://render.com)
- Regístrate gratis
- Verifica tu email

### 3️⃣ Desplegar con Blueprint

1. En Render Dashboard: **New +** → **Blueprint**
2. Conecta tu repositorio GitHub
3. Selecciona `biblioteca-microservices`
4. Render detectará `render.yaml` automáticamente
5. Click **Apply**

### 4️⃣ Inicializar Bases de Datos

Espera 5 minutos a que se creen las bases de datos, luego:

#### Inventory DB:
```bash
# Obtén la connection string en Render Dashboard → biblioteca-inventory-db
psql [CONNECTION_STRING]

# Dentro de psql, ejecuta:
\i database/init-inventory-db.sql
\q
```

#### Loan DB:
```bash
psql [CONNECTION_STRING]
\i database/init-loan-db.sql
\q
```

### 5️⃣ ¡Probar!

```bash
# Reemplaza con tu URL real de Render
curl https://biblioteca-api-gateway.onrender.com/api/books
```

---

## 🌐 Obtener tus URLs

Una vez desplegado, ve a Render Dashboard:

- **API Gateway**: Click en `biblioteca-api-gateway` → Copia la URL
- **Inventory Service**: Click en `biblioteca-inventory-service` → Copia la URL  
- **Loan Service**: Click en `biblioteca-loan-service` → Copia la URL

---

## 📱 Actualizar Postman

Importa la colección y crea un Environment:

**Name**: Render Production

**Variables**:
```
gateway_url = https://biblioteca-api-gateway.onrender.com
```

---

## ⚠️ Primera Petición

La primera petición tomará 30-60 segundos (servicios en sleep). Después será rápido.

---

## 🆘 Problemas Comunes

### "Service failed to start"
- Espera 2-3 minutos más
- Revisa logs en Render Dashboard → Service → Logs

### "Database connection error"
- Verifica que hayas ejecutado los scripts SQL
- Confirma que las bases de datos estén en estado "Available"

### "502 Bad Gateway"
- Espera 60 segundos (servicio despertando)
- Si persiste, verifica logs

---

## 💡 Tips

1. **Primera vez**: Todo toma ~10 minutos en total
2. **Despliegues futuros**: Solo haz `git push`
3. **Logs**: Siempre revisa los logs en Render Dashboard
4. **Free tier**: Servicios duermen después de 15 min sin uso

---

Para más detalles, ver **DESPLIEGUE-RENDER.md**

