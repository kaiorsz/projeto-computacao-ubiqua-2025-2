# ============================================
# Dockerfile for HemogramaUbiquoApplication
# ============================================

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration files first (for dependency caching)
COPY pom.xml .

# Download dependencies (will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Compile the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ============================================
# Stage 2: Production image
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create data directory for H2 database with correct permissions
RUN mkdir -p /app/data && chown -R spring:spring /app/data

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership of app.jar
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8081

# Default environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/hemograma-api/actuator/health || exit 1

# Command to start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

