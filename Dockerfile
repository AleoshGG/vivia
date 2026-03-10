# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos el jar desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Definimos variables de entorno (pueden ser sobrescritas al correr el contenedor)
ENV DB_URL=jdbc:postgresql://db-alexis:5432/alexis
ENV DB_USER=postgres
ENV DB_PASSWORD=1234567
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]