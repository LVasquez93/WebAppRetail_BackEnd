# Etapa 1: Build con Maven y Eclipse Temurin 17
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Descargar dependencias para aprovechar la caché de capas de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el archivo JAR ejecutable
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución ligera con JRE 17 y soporte de fuentes para PDF
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar fontconfig y fuentes TrueType Dejavu para el renderizado correcto de PDFs en Linux (OpenHTMLtoPDF / PDFBox)
RUN apk add --no-cache fontconfig ttf-dejavu

# Copiar el jar compilado desde la etapa de construcción
COPY --from=build /app/target/cotizador-backend-*.jar app.jar

# Render inyecta la variable de entorno PORT automáticamente
ENV PORT=8080
EXPOSE ${PORT}

# JVM flags optimizados para contenedores (control de RAM) y modo headless para AWT/PDF
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.awt.headless=true -jar app.jar"]
