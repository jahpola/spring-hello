# Docker Compose Setup Guide

## Overview

This project now includes a complete Docker Compose setup for both development and production environments.

## Configuration Files

### 📄 `compose.yaml` (Full Stack)
Complete environment with both database and application:
```bash
docker-compose up -d
```
- **Database**: PostgreSQL 18 with persistent storage
- **Application**: Spring Boot app with health checks
- **Network**: Isolated bridge network
- **Volumes**: Persistent PostgreSQL data

### 📄 `compose.dev.yaml` (Database Only)
Development setup - database only for local Spring Boot development:
```bash
docker-compose -f compose.dev.yaml up -d
./gradlew bootRun
```

### 📄 `Dockerfile`
Optimized multistage Spring Boot container:
- **Stage 1 (Builder)**: Eclipse Temurin JDK 25 Alpine for building
- **Stage 2 (Runtime)**: Eclipse Temurin JRE 25 Alpine for running
- **Layer optimization**: Uses Spring Boot's layered JAR extraction
- **Security**: Runs as non-root user
- **Performance**: Container-aware JVM settings
- **Health checks**: Includes curl for monitoring
- **Size**: ~50% smaller than single-stage build

### 📄 `application-docker.yaml`
Docker-specific Spring profile:
- Database connection to Docker network
- Enhanced logging for debugging
- Disables auto-discovery conflicts

## Usage Instructions

### Option 1: Full Stack (Recommended for Testing)
```bash
# Start both database and application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Option 2: Database Only (Recommended for Development)
```bash
# Start only PostgreSQL
docker-compose -f compose.dev.yaml up -d

# Run Spring Boot locally
./gradlew bootRun

# Stop database
docker-compose -f compose.dev.yaml down
```

### Option 3: Local Development (No Docker)
```bash
# Uses Testcontainers automatically during testing
./gradlew test

# For local development without Docker
./gradlew bootRun  # Will use H2 if PostgreSQL not available
```

## Services

### 🐘 PostgreSQL Database
- **Image**: `postgres:18-alpine`
- **Port**: `5432`
- **Database**: `product`
- **Username**: `sa`
- **Password**: `passu`
- **Features**:
  - Persistent data storage
  - Health checks
  - Initialization scripts
  - Network isolation

### 🚀 Spring Boot Application
- **Port**: `8081`
- **Health Check**: `/actuator/health`
- **Features**:
  - Depends on healthy database
  - Auto-restart on failure
  - Environment-specific configuration
  - Integrated logging

## Multistage Docker Build

### 🏗️ Build Optimization

The Dockerfile uses a sophisticated multistage build approach that provides significant benefits:

#### Stage 1: Builder (eclipse-temurin:25-jdk-alpine)
- **Purpose**: Compile and build the application
- **Size**: ~350MB (includes full JDK and build tools)
- **Contents**: 
  - Full JDK 25 for compilation
  - Gradle wrapper and dependencies
  - Source code compilation
  - Spring Boot layered JAR extraction

#### Stage 2: Runtime (eclipse-temurin:25-jre-alpine)
- **Purpose**: Run the optimized application
- **Size**: ~180MB (JRE only, no build tools)
- **Contents**:
  - JRE 25 (smaller than JDK)
  - Extracted application layers
  - Non-root user for security
  - Health check utilities

### 🎯 Benefits

| Aspect | Single Stage | Multistage | Improvement |
|--------|-------------|------------|-------------|
| **Final Image Size** | ~400MB | ~180MB | **55% smaller** |
| **Security** | Root user | Non-root user | **Enhanced** |
| **Layer Caching** | Basic | Optimized | **Faster builds** |
| **Dependencies** | Always rebuilt | Cached separately | **Much faster** |
| **Production Ready** | Basic | Optimized | **Production grade** |

### 🔧 Layer Optimization

Uses Spring Boot's layered JAR feature for optimal Docker layer caching:

```dockerfile
# Layers (from least to most frequently changing):
1. Dependencies (external libraries) - cached longest
2. Spring Boot loader - rarely changes  
3. Snapshot dependencies - changes occasionally
4. Application code - changes most frequently
```

### 🛡️ Security Features

- **Non-root user**: Runs as user `spring` (UID 1001)
- **Minimal attack surface**: JRE-only runtime (no build tools)
- **Container-aware JVM**: Optimized memory settings
- **Health monitoring**: Built-in curl for health checks

### ⚡ Performance Optimizations

```dockerfile
# Container-aware JVM settings
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

- **Memory management**: Uses 75% of available container memory
- **Container support**: JVM aware of container limits
- **Faster startup**: Layered approach reduces cold start time
- **Efficient caching**: Dependencies cached separately from code

## Environment Variables

The application supports the following environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://database:5432/product` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `sa` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `passu` | Database password |
| `SPRING_PROFILES_ACTIVE` | `docker` | Active Spring profile |

## Networking

- **Network Name**: `spring-hello-network`
- **Type**: Bridge network
- **Services**: `database` and `app` can communicate by service name
- **Port Mapping**: 
  - Database: `localhost:5432 -> container:5432`
  - Application: `localhost:8081 -> container:8081`

## Data Persistence

- **Volume**: `postgres_data`
- **Mount Point**: `/var/lib/postgresql/data`
- **Initialization**: SQL scripts auto-executed on first run
  - `src/main/resources/db/migration/V1__Create_product_table.sql`
  - `src/main/resources/db/migration/V2__Add_product.sql`

## Health Checks

### Database Health Check
```bash
pg_isready -U sa -d product
```
- Interval: 10s
- Timeout: 5s
- Retries: 5
- Start period: 30s

### Application Health Check
```bash
curl -f http://localhost:8081/actuator/health
```
- Interval: 30s
- Timeout: 10s
- Retries: 3
- Start period: 60s

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using port 5432
   lsof -i :5432
   
   # Kill the process or change port in compose.yaml
   ```

2. **Database Connection Failed**
   ```bash
   # Check database logs
   docker-compose logs database
   
   # Ensure database is healthy
   docker-compose ps
   ```

3. **Application Won't Start**
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Verify database is ready
   docker-compose exec database pg_isready -U sa -d product
   ```

4. **Build Failures**
   ```bash
   # Clean build
   docker-compose build --no-cache app
   
   # Or rebuild everything
   docker-compose up --build
   ```

### Useful Commands

```bash
# View all containers
docker-compose ps

# Follow logs for specific service
docker-compose logs -f database
docker-compose logs -f app

# Execute commands in containers
docker-compose exec database psql -U sa -d product
docker-compose exec app curl localhost:8081/actuator/health

# Restart specific service
docker-compose restart app

# Update and restart
docker-compose pull
docker-compose up -d
```

## Development Workflow

1. **Initial Setup**
   ```bash
   docker-compose -f compose.dev.yaml up -d
   ```

2. **Development**
   ```bash
   ./gradlew bootRun
   # Make code changes...
   # App auto-restarts with Spring Boot DevTools
   ```

3. **Testing**
   ```bash
   ./gradlew test
   # Uses Testcontainers automatically
   ```

4. **Production Testing**
   ```bash
   docker-compose up --build
   ```

## Next Steps

- Consider adding Redis for caching
- Add monitoring with Prometheus/Grafana
- Implement log aggregation
- Add backup strategies for production
- Configure SSL/TLS for production deployment