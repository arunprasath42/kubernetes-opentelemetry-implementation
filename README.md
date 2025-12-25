# Kubernetes Observability with OpenTelemetry

A cloud-neutral, production-ready approach to observability for Kubernetes using OpenTelemetry.

This repository contains examples and guidance for instrumenting Java applications, deploying the OpenTelemetry Collector as a DaemonSet, and shipping traces, metrics, and logs to any backend.

---

## Why observability matters

Kubernetes applications are distributed by design. A single user request may traverse multiple services, pods, and nodes. When problems happen, having only one telemetry signal (logs, metrics, or traces) is not enough:

- Logs alone often lack request context.
- Metrics alone can show symptoms but not root cause.
- Traces alone show request flow but not system health.

Observability requires logs, metrics, and traces working together so you can quickly find the root cause and reduce mean time to resolution (MTTR).

---

## What is OpenTelemetry?

OpenTelemetry (OTel) is a vendor-neutral, open standard for generating and exporting telemetry data:

- Traces — individual request flows across services
- Metrics — performance and resource signals
- Logs — runtime and application events

Core principle: instrument once, export anywhere. Apps can be decoupled from backends using the OpenTelemetry Collector.

---

## High-level architecture

Applications (instrumented via SDKs or agents) -> OTLP -> OpenTelemetry Collector -> Observability backend (Prometheus, Grafana, Jaeger, Tempo, commercial SaaS)

The Collector centralizes processing (batching, sampling, enrichment) and can export to multiple backends.

---

## Telemetry data flow

1. Application emits telemetry via OpenTelemetry SDK or agent.
2. SDK / agent sends OTLP to the Collector.
3. Collector processes and exports data to one or more backends.

This minimizes application overhead and keeps your instrumentation vendor-neutral.

---

## Instrumentation

### Automatic instrumentation (agent-based) — Java example

Automatic instrumentation is the fastest way to get visibility with zero code changes.

Run your JVM with the OpenTelemetry Java agent:

```bash
java -javaagent:/otel/opentelemetry-javaagent.jar \
     -Dotel.service.name=sample-service \
     -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
     -jar app.jar
```

What you get:
- HTTP request traces
- Error tracking
- Runtime and JVM metrics
- No code changes required

### Manual instrumentation (example)

Manual instrumentation is useful for business-critical logic and adding rich attributes.

```java
Span span = tracer.spanBuilder("processOrder").startSpan();
try (Scope scope = span.makeCurrent()) {
    // business logic
    span.setAttribute("order.type", "premium");
} finally {
    span.end();
}
```

Use manual spans to answer:
- Where is latency introduced?
- Which operation is slow?
- Which flow is failing?

---

## Why the OpenTelemetry Collector?

The Collector is the telemetry control plane. It:

- Receives telemetry from apps
- Batches and processes data
- Enriches telemetry with metadata
- Exports to one or more backends

This reduces app overhead and enables flexible backend routing and processing.

---

## Deploying the Collector as a DaemonSet

For node-level telemetry (container, host metrics), or when you want one collector per node, run the Collector as a DaemonSet.

Benefits:
- One collector pod per node
- Local scraping of node metrics
- Lower network latency
- Higher reliability during node failures

DaemonSets are ideal for infrastructure-level observability in large clusters.

### Example DaemonSet manifest

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: otel-collector
  namespace: observability
spec:
  selector:
    matchLabels:
      app: otel-collector
  template:
    metadata:
      labels:
        app: otel-collector
    spec:
      containers:
        - name: otel-collector
          image: otel/opentelemetry-collector:latest
          args:
            - "--config=/conf/collector.yaml"
          ports:
            - containerPort: 4317
            - containerPort: 4318
          volumeMounts:
            - name: config
              mountPath: /conf
      volumes:
        - name: config
          configMap:
            name: otel-collector-config
```

---

## Sample Collector configuration

This example config receives OTLP (gRPC & HTTP), batches data, and forwards it to a generic OTLP exporter. Replace `<OBSERVABILITY_BACKEND_ENDPOINT>` with your backend endpoint.

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:

processors:
  batch:

exporters:
  otlp:
    endpoint: <OBSERVABILITY_BACKEND_ENDPOINT>

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlp]
```

---

## Helm-based deployment (recommended)

Helm provides version-controlled installs, easy upgrades, and environment overrides.

Add the chart repo and install the Collector as a DaemonSet:

```bash
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo update

helm install otel-collector open-telemetry/opentelemetry-collector \
  --namespace observability \
  --create-namespace \
  --values values.yaml
```

### values.yaml (DaemonSet mode)

```yaml
mode: daemonset

image:
  repository: otel/opentelemetry-collector

config:
  receivers:
    otlp:
      protocols:
        grpc:
        http:
  processors:
    batch:
  exporters:
    otlp:
      endpoint: <OBSERVABILITY_BACKEND_ENDPOINT>
  service:
    pipelines:
      traces:
        receivers: [otlp]
        processors: [batch]
        exporters: [otlp]
      metrics:
        receivers: [otlp]
        processors: [batch]
        exporters: [otlp]
      logs:
        receivers: [otlp]
        processors: [batch]
        exporters: [otlp]
```

---

## Visualizing and alerting

Telemetry can be exported to open-source tools (Prometheus, Grafana, Jaeger, Tempo) or commercial platforms. Typical dashboards and alerts track:

- Latency (p95 / p99)
- Error rate
- Throughput
- CPU / memory usage
- Service dependencies and topology

Define alerts as code (Terraform, Grafana provisioning, Prometheus rules) to keep configurations versioned and auditable.

---

## Ownership model

- Application teams: instrument code, define service names and custom metrics
- Platform / SRE teams: deploy and manage collectors, handle scaling, access to backends

This separation keeps instrumentation consistent while centralizing operational responsibilities.

---

## Quickstart — Java app with Collector

1. Deploy the Collector (DaemonSet) in the `observability` namespace using the provided Helm chart or DaemonSet manifest.
2. Run your Java app with the OpenTelemetry Java agent and point it to the Collector:

```bash
java -javaagent:/otel/opentelemetry-javaagent.jar \
     -Dotel.service.name=my-java-service \
     -Dotel.exporter.otlp.endpoint=http://otel-collector.observability.svc.cluster.local:4317 \
     -jar myapp.jar
```

3. Verify traces, metrics, and logs appear in your configured backend.

---

## What this achieves

- Faster root-cause analysis
- Consistent observability across clusters (GKE, EKS, AKS, on-prem)
- Reduced incident resolution time
- Scalable, vendor-neutral telemetry collection

---

## Files in this repository

- README.md — this file (how-to, examples, references)
- (Optional) collector-config.yaml — example Collector config
- (Optional) values.yaml — Helm values for DaemonSet mode

---

## Contributing

Contributions and improvements are welcome. Open a PR or raise an issue to propose changes.

---

## License

MIT
