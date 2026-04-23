# Stage 1: Build stage
FROM eclipse-temurin:25-jdk-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files first (for better layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (this layer will be cached unless dependencies change)
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./gradlew build -x test --no-daemon

# Extract the built JAR file
RUN mkdir -p build/dependency && \
    cd build/dependency && \
    java -Djarmode=layertools -jar ../libs/*-SNAPSHOT.jar extract

# Stage 2: Runtime stage
FROM eclipse-temurin:25-jre-alpine AS runtime

# Install curl for health checks and create non-root user
RUN apk add --no-cache curl && \
    addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Set the working directory
WORKDIR /app

# Copy the extracted layers from the builder stage
COPY --from=builder app/build/dependency/dependencies/ ./
COPY --from=builder app/build/dependency/spring-boot-loader/ ./
COPY --from=builder app/build/dependency/snapshot-dependencies/ ./
COPY --from=builder app/build/dependency/application/ ./

# Change ownership to the spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose the port
EXPOSE 8081

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]