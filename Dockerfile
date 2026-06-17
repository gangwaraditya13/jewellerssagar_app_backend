# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

COPY src ./src

RUN ./mvnw -B clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -jar /app/app.jar --server.port=${PORT:-8080}"]