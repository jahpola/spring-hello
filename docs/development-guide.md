# Development Guide

## Prerequisites
- Java 25
- Docker Desktop, Rancher Desktop, or Colima
- Gradle 9.4.0 (via wrapper)

## Quick Start

### Option 1: Docker Compose (Recommended)
```bash
# Database only (for development)
docker-compose -f compose.dev.yaml up -d
./gradlew bootRun

# Full stack (app + database)
docker-compose up -d
```

### Option 2: Testcontainers
```bash
# Automatically starts PostgreSQL in container
./gradlew bootRun
```

## Building

### Local Build
```bash
./gradlew build
```

### Docker Image
```bash
# Using Gradle
./gradlew bootBuildImage

# Using Docker
docker build -t spring-hello .
```

## Testing

### Run Tests
```bash
./gradlew test
```

### Test Coverage
```bash
./gradlew jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html
```

### Integration Tests
Uses Testcontainers for PostgreSQL. Tests automatically start required containers.

## Configuration

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/product
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=passu
SPRING_PROFILES_ACTIVE=docker
```

### Profiles
- **default**: Local development with compose.dev.yaml
- **docker**: Container deployment
- **kubernetes**: K8s with service discovery

## Database Management

### Flyway Migrations
Located in `src/main/resources/db/migration/`

```bash
# Apply migrations
./gradlew flywayMigrate

# Check status
./gradlew flywayInfo

# Clean database (dev only)
./gradlew flywayClean
```

### Access Database
```bash
# Via Docker
docker exec -it spring-hello-postgres psql -U sa -d product

# Via local client
psql -h localhost -p 5432 -U sa -d product
```

## Kubernetes Development

### Prerequisites
- Local Kubernetes (Docker Desktop, Rancher Desktop, or Minikube)
- Skaffold

### Deploy
```bash
# Continuous development
skaffold dev --port-forward

# One-time deployment
kubectl apply -k manifests/overlays/local
```

### Access Application
```bash
# Port forward
kubectl port-forward svc/hello 8081:8081

# Test
curl http://localhost:8081/actuator/health
```

## Testcontainers Setup

### Rancher Desktop
```bash
export DOCKER_HOST=unix://$HOME/.rd/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export TESTCONTAINERS_HOST_OVERRIDE=$(rdctl shell ip a show vznat | awk '/inet / {sub("/.*",""); print $2}')
```

### Colima
```bash
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export TESTCONTAINERS_HOST_OVERRIDE=$(colima ls -j | jq -r '.address')
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
```

## Code Style

### Formatting Rules
- Blank lines: Separate logical blocks
- Line length: Maximum 120 characters
- Style: IntelliJ IDEA default for Java

### Apply Formatting
Use IntelliJ IDEA's "Reformat Code" (Ctrl+Alt+L / Cmd+Option+L)

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 8081
lsof -i :8081

# Kill process
kill -9 <PID>
```

### Database Connection Issues
```bash
# Check database is running
docker ps | grep postgres

# Check logs
docker logs spring-hello-postgres

# Restart database
docker-compose -f compose.dev.yaml restart database
```

### Testcontainers Not Working
- Ensure Docker is running
- Check Docker socket permissions
- Set environment variables for your container runtime
- Try: `docker ps` to verify Docker access

### Build Failures
```bash
# Clean build
./gradlew clean build

# Skip tests
./gradlew build -x test

# Refresh dependencies
./gradlew build --refresh-dependencies
```

## Useful Commands

```bash
# Check application health
curl http://localhost:8081/actuator/health

# Create product
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":10.99,"inStock":true}'

# Get all products
curl http://localhost:8081/api/products

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down
```
