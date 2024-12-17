# spring-hello

Spring Cloud rest controller. Registers itself into Spring Cloud Gateway

## installation
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
