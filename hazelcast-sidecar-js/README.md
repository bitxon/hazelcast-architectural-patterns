# Hazelcast Sidecar Pattern (JS)

---

## Build & Run

<details>
  <summary><b>Skaffold</b></summary>

Execute Skaffold in dev mode

```bash
skaffold dev
```

</details>

<details>
  <summary><b>Kubectl</b></summary>

1. Build Docker image and Push

```bash
docker build -t bitxon/app-hz-sidecar-js:latest .
docker push bitxon/app-hz-sidecar-js:latest
```

2. Deploy to Kubernetes

```bash
# Apply hazelcast RBAC
kubectl apply -f k8s/hazelcast-rbac.yaml
# Create hazelcast configmap
kubectl apply -f k8s/hazelcast-configmap.yaml
# Expose hazelcast(5701) for discovery
kubectl apply -f k8s/hazelcast-service.yaml
# Create application deployment
kubectl apply -f k8s/app-deployment.yaml
# Expose application(8080)
kubectl apply -f k8s/app-service.yaml
```

3. Expose service port to local machine

```bash
kubectl port-forward service/application-service 8080:8080
```

Cleanup

```bash
kubectl delete -f k8s/app-service.yaml
kubectl delete -f k8s/app-deployment.yaml
kubectl delete -f k8s/hazelcast-service.yaml
kubectl delete -f k8s/hazelcast-configmap.yaml
kubectl delete -f k8s/hazelcast-rbac.yaml
```

</details>

---

## Test your setup

```bash
# Put Value
curl --request PUT 'http://localhost:8080/cache/key1/valueA'
# Get Value
curl --request GET 'http://localhost:8080/cache/key1'
# Put Value to Locked cache (5 seconds long)
curl --request PUT 'http://localhost:8080/fenced-lock-cache/key1/valueB'
# Get Value from Locked cache (max wait 6 seconds)
curl --request GET 'http://localhost:8080/fenced-lock-cache/key1'
# Scale application
kubectl scale deployment application-deployment --replicas=5
```

---

## Useful links

- [Hazelcast JS client](https://github.com/hazelcast/hazelcast-nodejs-client/blob/master/DOCUMENTATION.md)
- [Latest RBAC](https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml)
- [Hazelcast Kubernetes Discovery](https://github.com/hazelcast/hazelcast-kubernetes)
- [How to use System Properties](https://docs.hazelcast.com/hazelcast/5.2/configuration/configuring-with-system-properties)
- [Full Example of hazelcast-client.yaml](https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/main/resources/hazelcast-client-full-example.yaml)
