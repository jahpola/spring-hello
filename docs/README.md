# Spring Hello Documentation

Complete documentation for the Spring Hello product management REST API.

## Quick Links

- [Architecture](architecture.md) - System design and components
- [API Reference](api-reference.md) - REST endpoints and examples
- [Development Guide](development-guide.md) - Local setup and testing
- [Deployment Guide](deployment-guide.md) - Docker and Kubernetes deployment

## Project Overview

Spring Boot REST API for product management with:
- CRUD operations for products
- PostgreSQL database with Flyway migrations
- Kubernetes-ready with Spring Cloud
- Docker Compose for local development
- Testcontainers for testing

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 4.0.x |
| Spring Cloud | 2025.1.0 |
| Gradle | 9.4.0 |
| PostgreSQL | 18.x |

## Getting Started

### 1. Clone and Setup
```bash
git clone <repository-url>
cd spring-hello
```

### 2. Start Database
```bash
docker-compose -f compose.dev.yaml up -d
```

### 3. Run Application
```bash
./gradlew bootRun
```

### 4. Test API
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/api/products
```

## Documentation Structure

### [Architecture](architecture.md)
- System overview
- Component structure
- Database schema
- Configuration profiles
- Observability setup

### [API Reference](api-reference.md)
- All REST endpoints
- Request/response examples
- Validation rules
- Error responses
- Health checks

### [Development Guide](development-guide.md)
- Prerequisites and setup
- Building and testing
- Database management
- Kubernetes development
- Testcontainers configuration
- Code style guidelines
- Troubleshooting

### [Deployment Guide](deployment-guide.md)
- Docker deployment
- Kubernetes deployment
- Configuration management
- Health checks
- Scaling strategies
- Monitoring setup
- Production considerations
- Rollback procedures

## Additional Documentation

### Existing Docs
- [API Documentation](api-documentation.md) - OpenAPI/Swagger details
- [Docker Setup](docker-setup.md) - Container configuration
- [Enhanced Validation Errors](enhanced-validation-errors.md) - Error handling
- [Multistage Build](multistage-build.md) - Docker optimization

## Key Features

### REST API
- Full CRUD operations
- Pagination support
- Input validation
- XSS protection
- Custom error handling

### Database
- PostgreSQL 18.x
- Flyway migrations
- JPA/Hibernate
- Connection pooling

### Deployment
- Docker Compose
- Kubernetes manifests
- Skaffold support
- Health probes
- Graceful shutdown

### Observability
- Actuator endpoints
- Micrometer metrics
- OpenTelemetry tracing
- Prometheus integration

### Development
- Testcontainers
- Hot reload
- Multiple profiles
- Code coverage

## Common Tasks

### Create Product
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stockQuantity": 50,
    "inStock": true
  }'
```

### Run Tests
```bash
./gradlew test
```

### Build Docker Image
```bash
./gradlew bootBuildImage
```

### Deploy to Kubernetes
```bash
skaffold dev --port-forward
```

## Support

For issues and questions:
1. Check [Development Guide](development-guide.md) troubleshooting section
2. Review application logs
3. Verify configuration
4. Check database connectivity

## Contributing

1. Follow code style guidelines in [Development Guide](development-guide.md)
2. Write tests for new features
3. Update documentation
4. Ensure all tests pass

## License

See [LICENSE](../LICENSE) file for details.
