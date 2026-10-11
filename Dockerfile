# Etapa 1: compilar el proyecto con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -B clean package -DskipTests

# Etapa 2: imagen final, solo con el JAR
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/lab12026p.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]