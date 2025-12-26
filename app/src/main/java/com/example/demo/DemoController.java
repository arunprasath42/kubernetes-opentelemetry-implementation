package com.example.demo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final Tracer tracer;

    public DemoController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("demo-service");
    }

    @GetMapping("/hello")
    public String hello() {
        Span span = tracer.spanBuilder("hello-endpoint").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("endpoint", "/hello");
            span.setAttribute("custom.attribute", "interview-poc");
            return "Hello from OpenTelemetry Kubernetes POC";
        } finally {
            span.end();
        }
    }
}
