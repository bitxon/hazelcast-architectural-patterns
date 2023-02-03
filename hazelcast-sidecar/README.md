# Hazelcast Sidecar Pattern

1. Build Java app
```bash
mvn clean install
```

2. Build Docker image and Push
```bash
# Don't forget to customize image name
docker build -t bitxon/app-hz-sidecar:latest .
docker push bitxon/app-hz-sidecar:latest
```

3. Deploy to Kubernetes
```bash
# Apply RBAC and expose(5701) via headless service for discovery
kubectl apply -f k8s/hazelcast-rbac.yaml
kubectl apply -f k8s/hazelcast-service.yaml
# Create app deployment and expose(8080) via service
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```


# Test your setup
```bash
# Put new Value { 1 : AAAAA }
curl --request PUT 'http://localhost:8080/cache/1/value/AAAAA'
# Get Value
curl --request GET 'http://localhost:8080/cache/1'
# Get Value with Lock (wait 10 seconds)
curl --request GET 'http://localhost:8080/locked-cache/1'
# Scale application
kubectl scale deployment app-hz-sidecar --replicas=5
```

# Useful links

- [Latest RBAC](https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml)
- [Hazelcast Kubernetes Discovery](https://github.com/hazelcast/hazelcast-kubernetes)
- [Full Example of hazelcast.yaml](https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/main/resources/hazelcast-full-example.yaml)
