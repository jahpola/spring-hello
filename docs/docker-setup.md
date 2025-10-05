# Docker Compose Setup Guide

## Overview

This project now includes a complete Docker Compose setup for both development and production environments.

## Configuration Files

### 📄 `compose.yaml` (Full Stack)
Complete environment with both database and application:
```bash
docker-compose up -d
```
- **Database**: PostgreSQL 17 with persistent storage
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
Optimized Spring Boot container:
- Uses Eclipse Temurin JDK 21 Alpine
- Includes curl for health checks
- Exposes port 8081

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
- **Image**: `postgres:17-alpine`
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
  - `V1__Create_product_table.sql`
  - `V2__Add_product.sql`

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