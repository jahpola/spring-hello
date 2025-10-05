# Use the official OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Copy the source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Copy the built JAR file
RUN cp build/libs/*.jar app.jar

# Expose the port
EXPOSE 8081

# Add curl for health checks
RUN apk add --no-cache curl

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]