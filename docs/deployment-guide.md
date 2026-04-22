# Deployment Guide

## Docker Deployment

### Build Image
```bash
# Using Gradle (Spring Boot Buildpacks)
./gradlew bootBuildImage
# Output: public.ecr.aws/kerminator/spring-hello:0.0.1-SNAPSHOT

# Using Dockerfile
docker build -t spring-hello:latest .
```

### Run Container
```bash
docker run -d \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/product \
  -e SPRING_DATASOURCE_USERNAME=sa \
  -e SPRING_DATASOURCE_PASSWORD=passu \
  --name spring-hello \
  spring-hello:latest
```

### Docker Compose
```bash
# Start full stack
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

## Kubernetes Deployment

### Prerequisites
- Kubernetes cluster (local or cloud)
- kubectl configured
- PostgreSQL database (external or in-cluster)

### Using Kustomize

**Local Environment**:
```bash
kubectl apply -k manifests/overlays/local
```

**Dev Environment**:
```bash
kubectl apply -k manifests/overlays/dev
```

**Base Resources**:
```bash
kubectl apply -k manifests/base
```

### Using Skaffold

**Development Mode**:
```bash
# Continuous deployment with hot reload
skaffold dev --port-forward
```

**Deploy Once**:
```bash
skaffold run
```

**Delete Deployment**:
```bash
skaffold delete
```

### Manual Deployment

```bash
# Create namespace
kubectl create namespace spring-hello

# Apply manifests
kubectl apply -f manifests/base/configmap.yaml -n spring-hello
kubectl apply -f manifests/base/sa.yaml -n spring-hello
kubectl apply -f manifests/base/role.yaml -n spring-hello
kubectl apply -f manifests/base/rolebinding.yaml -n spring-hello
kubectl apply -f manifests/base/deployment.yaml -n spring-hello
kubectl apply -f manifests/base/service.yaml -n spring-hello
```

### Verify Deployment

```bash
# Check pods
kubectl get pods -n spring-hello

# Check services
kubectl get svc -n spring-hello

# View logs
kubectl logs -f deployment/hello -n spring-hello

# Check health
kubectl port-forward svc/hello 8081:8081 -n spring-hello
curl http://localhost:8081/actuator/health
```

## Configuration

### Environment Variables

**Required**:
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database user
- `SPRING_DATASOURCE_PASSWORD`: Database password

**Optional**:
- `SPRING_PROFILES_ACTIVE`: Active profile (docker, kubernetes)
- `JAVA_OPTS`: JVM options
- `SERVER_PORT`: Application port (default: 8081)

### ConfigMap (Kubernetes)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: hello-config
data:
  SPRING_PROFILES_ACTIVE: kubernetes
  SERVER_PORT: "8081"
```

### Secrets (Kubernetes)
```bash
kubectl create secret generic db-credentials \
  --from-literal=username=sa \
  --from-literal=password=passu \
  -n spring-hello
```

### Secret RBAC Scope
- ServiceAccount `hello` is limited to read only one secret: `db-credentials`
- Secret permissions are restricted to `get` only (no `list` or `watch`)
- Access to any other secret name in the namespace is denied by RBAC

**Verify RBAC**:
```bash
# Expected: yes
kubectl auth can-i get secret db-credentials \
  --as=system:serviceaccount:spring-hello:hello \
  -n spring-hello

# Expected: no
kubectl auth can-i get secret some-other-secret \
  --as=system:serviceaccount:spring-hello:hello \
  -n spring-hello

# Expected: no
kubectl auth can-i list secrets \
  --as=system:serviceaccount:spring-hello:hello \
  -n spring-hello
```

## Health Checks

### Liveness Probe
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8081
  initialDelaySeconds: 30
  periodSeconds: 10
```

### Readiness Probe
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8081
  initialDelaySeconds: 20
  periodSeconds: 5
```

## Scaling

### Horizontal Pod Autoscaler
```bash
kubectl autoscale deployment hello \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n spring-hello
```

### Manual Scaling
```bash
kubectl scale deployment hello --replicas=3 -n spring-hello
```

## Monitoring

### Prometheus Metrics
Exposed at `/actuator/prometheus`

**ServiceMonitor** (Prometheus Operator):
```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: hello-metrics
spec:
  selector:
    matchLabels:
      app: hello
  endpoints:
  - port: http
    path: /actuator/prometheus
```

### Logs
```bash
# Kubernetes
kubectl logs -f deployment/hello -n spring-hello

# Docker
docker logs -f spring-hello-app
```

## Production Considerations

### Resource Limits
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

### JVM Settings
```bash
JAVA_OPTS="-XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -Xlog:gc*:file=/tmp/gc.log"
```

### Database Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### Security
- Use secrets for credentials
- Enable HTTPS/TLS
- Run as non-root user (UID 1001)
- Scan images for vulnerabilities
- Keep dependencies updated

### Backup Strategy
- Regular database backups
- Flyway migration history
- Configuration backups
- Disaster recovery plan

## Rollback

### Kubernetes
```bash
# View rollout history
kubectl rollout history deployment/hello -n spring-hello

# Rollback to previous version
kubectl rollout undo deployment/hello -n spring-hello

# Rollback to specific revision
kubectl rollout undo deployment/hello --to-revision=2 -n spring-hello
```

### Docker Compose
```bash
# Use specific image tag
docker-compose down
# Edit compose.yaml to use previous tag
docker-compose up -d
```

## Troubleshooting

### Pod Not Starting
```bash
kubectl describe pod <pod-name> -n spring-hello
kubectl logs <pod-name> -n spring-hello
```

### Database Connection Failed
- Verify database is accessible
- Check credentials in secrets
- Verify network policies
- Test connection from pod: `kubectl exec -it <pod> -- curl database:5432`

### Out of Memory
- Increase memory limits
- Adjust JVM heap settings
- Check for memory leaks
- Review application logs
