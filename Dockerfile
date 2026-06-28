# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# wait-for-it para esperar infra antes de arrancar
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Copiamos el jar desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Entorno por defecto
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

# Espera RabbitMQ y la RDS (DB_HOST) antes de levantar Spring
ENTRYPOINT ["/bin/sh", "-c", \
  "/wait-for-it.sh vivia-rabbitmq:5672 -t 60 -- /wait-for-it.sh \"$DB_HOST:5432\" -t 60 -- java -Djava.security.egd=file:/dev/./urandom -jar app.jar"]