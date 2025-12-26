# Kubernetes Observability POC using OpenTelemetry

A minimal, production-style proof of concept demonstrating end-to-end observability in Kubernetes using OpenTelemetry.

This repository showcases how to instrument a Java application, deploy the OpenTelemetry Collector correctly, and design a cloud-agnostic telemetry pipeline that works across any Kubernetes platform.

---

## What This Project Demonstrates

- OpenTelemetry instrumentation in a Java application
- Automatic and manual tracing
- OpenTelemetry Collector deployed as a DaemonSet
- Helm-based deployment for operational consistency
- Cloud-neutral Kubernetes design (GKE / EKS / AKS)

This POC focuses on clarity, correctness, and real-world patterns.

---

## High-Level Architecture

1. Java application runs inside Kubernetes
2. Application emits traces and metrics using OpenTelemetry
3. Telemetry is sent using OTLP
4. OpenTelemetry Collector receives and processes data
5. Telemetry is exported to a backend (logging exporter for POC)

Applications never communicate directly with monitoring tools.

---

## Repository Structure

kubernetes-opentelemetry-poc/
├── app/
│ ├── Dockerfile
│ ├── pom.xml
│ └── src/main/java/com/example/demo
│ ├── DemoApplication.java
│ └── DemoController.java
├── k8s/
│ ├── app-deployment.yaml
│ ├── app-service.yaml
│ ├── otel-collector-daemonset.yaml
│ └── otel-collector-configmap.yaml
├── helm/
│ └── otel-collector/
│ └── values.yaml
└── README.md


---

---

## Java Application Instrumentation

### Automatic Instrumentation

The Java application uses the OpenTelemetry Java Agent for automatic instrumentation.

This provides:
- HTTP request tracing
- Error tracking
- JVM and runtime metrics
- Zero code changes

The agent is attached at JVM startup.

---

### Manual Instrumentation (Business Logic)

Manual tracing is added to highlight important application logic.

Example:
- A custom span is created for the `/hello` endpoint
- Attributes are added to enrich trace context

This demonstrates control beyond automatic instrumentation.

---

## Kubernetes Application Deployment

The application is deployed using a standard Kubernetes Deployment and Service.

Key points:
- OTLP endpoint is injected via environment variables
- Application remains backend-agnostic
- Works on any Kubernetes cluster

---

## OpenTelemetry Collector

The OpenTelemetry Collector acts as the telemetry control plane.

Responsibilities:
- Receive telemetry from applications
- Batch and process data
- Export telemetry to a backend

Using a collector:
- Reduces application overhead
- Improves scalability
- Decouples apps from observability tools

---

## Why the Collector Runs as a DaemonSet

The collector is deployed as a DaemonSet.

Reasoning:
- One collector pod per node
- Local telemetry ingestion
- Lower network latency
- Better resilience during node failures

This is a common production pattern for infrastructure-level observability.

---

## Collector Configuration

The collector is configured with:
- OTLP receiver (gRPC and HTTP)
- Batch processor
- Logging exporter (for POC visibility)

This setup is intentionally minimal and easy to understand.

---

## Helm-Based Deployment

Helm is used to deploy the OpenTelemetry Collector.

Why Helm:
- Version-controlled deployment
- Easy upgrades and rollbacks
- Environment-specific configuration
- Production-friendly operations

The collector runs in DaemonSet mode via Helm values.

---

## How to Run the POC

1. Build the Java application and Docker image
2. Deploy the OpenTelemetry Collector (DaemonSet or Helm)
3. Deploy the Java application
4. Call the `/hello` endpoint
5. Observe traces and metrics in collector logs

---

## What Interviewers Should Notice

This project demonstrates:
- Correct OpenTelemetry usage
- Kubernetes-native observability design
- Clear DaemonSet vs Deployment reasoning
- Helm-based operational maturity
- Cloud-agnostic thinking

---

## Interview-Ready Summary

"I built a Kubernetes observability POC using OpenTelemetry with a Java application. I combined automatic and manual instrumentation, deployed the OpenTelemetry Collector as a DaemonSet using Helm, and designed the solution to be cloud-agnostic so it works across GKE, EKS, and AKS."

---

## Final Notes

This repository is intentionally:
- Simple
- Clean
- Realistic
- Interview-ready

The platform may change.
The observability principles remain the same.
