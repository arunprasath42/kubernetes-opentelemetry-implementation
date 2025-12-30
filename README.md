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

---

## High-Level Architecture

1. A Java application runs inside Kubernetes  
2. The application emits traces and metrics using OpenTelemetry  
3. Telemetry is sent using OTLP  
4. The OpenTelemetry Collector receives and processes the data  
5. Telemetry is exported to a backend (logging exporter for this POC)  

Applications never communicate directly with monitoring tools.

---

## Repository Structure

The repository is intentionally organized to keep application code, Kubernetes manifests, and Helm configuration clearly separated.

### 📁 Application (`app/`)
- **Dockerfile** – Container image definition for the Java application  
- **pom.xml** – Maven build configuration  
- **src/** – Spring Boot application source code  
  - `DemoApplication.java` – Application entry point  
  - `DemoController.java` – REST endpoint with manual tracing  

### 📁 Kubernetes Manifests (`k8s/`)
- **app-deployment.yaml** – Application Deployment  
- **app-service.yaml** – Application Service  
- **otel-collector-daemonset.yaml** – OpenTelemetry Collector DaemonSet  
- **otel-collector-configmap.yaml** – Collector configuration  

### 📁 Helm Chart (`helm/otel-collector/`)
- **values.yaml** – Helm values enabling DaemonSet mode  

### 📄 Documentation
- **README.md** – Project overview and explanation  

---

## Java Application Instrumentation

### Automatic Instrumentation

The application uses the OpenTelemetry Java Agent for automatic instrumentation.

This provides:
- HTTP request tracing
- Error tracking
- JVM and runtime metrics
- Zero application code changes

The agent is attached at JVM startup.

---

### Manual Instrumentation

Manual tracing is added to highlight important business logic.

This includes:
- Custom spans for API endpoints  
- Meaningful attributes added to spans  

This demonstrates fine-grained control beyond automatic instrumentation.

---

## Kubernetes Application Deployment

The application is deployed using standard Kubernetes primitives.

Key design points:
- OTLP endpoint is injected via environment variables  
- The application remains observability-backend agnostic  
- The same manifests work across any Kubernetes platform  

---

## OpenTelemetry Collector

The OpenTelemetry Collector acts as the telemetry control plane.

Its responsibilities include:
- Receiving telemetry from applications  
- Batching and processing data  
- Exporting telemetry to a backend  

Using a collector:
- Reduces application overhead  
- Improves scalability  
- Decouples applications from observability tools  

---

## Why the Collector Runs as a DaemonSet

The collector is deployed as a **DaemonSet**.

This ensures:
- One collector pod runs on each node  
- Local telemetry ingestion with reduced latency  
- Better resilience during node failures  

This is a common production pattern for infrastructure-level observability.

---

## Collector Configuration

The collector configuration includes:
- OTLP receiver (gRPC and HTTP)  
- Batch processor  
- Logging exporter (used for POC visibility)  

The configuration is intentionally minimal and easy to extend.

---

## Helm-Based Deployment

Helm is used to deploy the OpenTelemetry Collector.

Benefits of using Helm:
- Version-controlled deployments  
- Easy upgrades and rollbacks  
- Environment-specific configuration  
- Production-friendly operations  

The collector is deployed in DaemonSet mode via Helm values.

---

## Running the POC

1. Build the Java application and Docker image  
2. Deploy the OpenTelemetry Collector (DaemonSet or Helm)  
3. Deploy the Java application  
4. Call the `/hello` endpoint  
5. Observe traces and metrics in the collector logs  

---
