# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
# Cache dependencies in docker layer
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/target/sagar-jewellery-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# JVM tuning options for low latency and high concurrency (G1GC, container-aware memory limit, exit on OOM)
ENTRYPOINT ["java", \
            "-XX:+UseG1GC", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:MinRAMPercentage=50.0", \
            "-XX:+ExitOnOutOfMemoryError", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]
