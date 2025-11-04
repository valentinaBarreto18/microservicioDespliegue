# Dockerfile para Inventory Service
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven del parent
COPY pom.xml ../pom.xml
COPY inventory-service/pom.xml ./pom.xml

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY inventory-service/src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

# Crear usuario no privilegiado
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el jar compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8081

# Configurar health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

