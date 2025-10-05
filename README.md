# spring-hello

Spring Cloud rest controller. Registers itself into Spring Cloud Gateway

## Quick Start

### Option 1: Docker Compose (Recommended)
```bash
# Start database only (for development)
docker-compose -f compose.dev.yaml up -d
./gradlew bootRun

# Or start full stack
docker-compose up -d
```

### Option 2: Local Development
```bash
# Uses Testcontainers automatically
./gradlew bootRun
./gradlew test
```

## Installation
1. uses gradlew
2. install some local kubernetes
3. run skaffold dev

### skaffold tips
skaffold dev --port-forward  


### Test container with rancher desktop

Getting test containers working with Rancher Desktop
```
export DOCKER_HOST=unix://$HOME/.rd/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export TESTCONTAINERS_HOST_OVERRIDE=$(rdctl shell ip a show vznat | awk '/inet / {sub("/.*",""); print $2}')
```

### Test container with Colima

Getting test containers working with Colima
```
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock                   
export TESTCONTAINERS_HOST_OVERRIDE=$(colima ls -j | jq -r '.address')
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"
```