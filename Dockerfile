# =========================
# 1️⃣ Build stage
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first (cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# =========================
# 2️⃣ Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
