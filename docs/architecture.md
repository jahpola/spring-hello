# Architecture Documentation

## Overview
Spring Boot REST API for product management with PostgreSQL database, designed for Kubernetes deployment.

## Technology Stack
- **Java**: 25
- **Spring Boot**: 4.0.x
- **Spring Cloud**: 2025.1.0
- **Database**: PostgreSQL 18.x
- **Build Tool**: Gradle 9.4.0
- **Container Runtime**: Docker with Testcontainers support

## Application Structure

### Layers
```
org.kerminator.hello
├── controllers/     # REST endpoints
├── service/         # Business logic
├── repository/      # Data access
├── model/           # JPA entities
└── exception/       # Error handling
```

### Components

**ProductController** (`/api/products`)
- CRUD operations for products
- Pagination support (default: 20 items/page)
- Validation with Jakarta Bean Validation
- Observability annotations for monitoring

**ProductService**
- Business logic layer
- Input sanitization (XSS prevention)
- Transaction management

**ProductRepository**
- Spring Data JPA interface
- Custom queries for price and stock filtering

**Product Entity**
- JPA entity mapped to `products` table
- Validation constraints on all fields
- BigDecimal for precise price handling

## Database

**Schema**: Managed by Flyway migrations (`src/main/resources/db/migration`)

**Product Table**:
- `id` (BIGINT, auto-increment)
- `name` (VARCHAR, required, 2-100 chars)
- `description` (VARCHAR, max 1000 chars)
- `price` (DECIMAL(10,2), required, min 0.01)
- `stock_quantity` (INTEGER)
- `in_stock` (BOOLEAN)

## Configuration

**Profiles**:
- `default`: Local development with Docker Compose
- `docker`: Container deployment
- `kubernetes`: K8s deployment with service discovery

**Key Settings**:
- Server port: 8081
- Graceful shutdown enabled
- Health probes: `/actuator/health`
- Flyway migrations: Auto-enabled

## Deployment

**Docker Compose**: Two-service stack (app + database)
- Development: `compose.dev.yaml` (database only)
- Full stack: `compose.yaml` (app + database)

**Kubernetes**: Manifests in `manifests/`
- Base configuration with Kustomize
- Overlays for dev/local environments
- RBAC, ConfigMap, Service resources

**Skaffold**: Continuous development workflow
- `skaffold dev --port-forward`

## Observability

**Actuator Endpoints**: `/actuator/health`, `/actuator/prometheus`
**Metrics**: Micrometer with @Observed annotations
**Tracing**: OpenTelemetry support (disabled by default)

## Security

- Input sanitization via HtmlUtils
- Bean validation on all inputs
- Non-root container user (UID 1001)
- Graceful error handling with custom exceptions
