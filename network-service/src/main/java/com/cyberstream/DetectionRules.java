package com.cyberstream;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import jakarta.inject.Inject;
import io.micrometer.core.instrument.MeterRegistry;

@ApplicationScoped
public class DetectionRules {

    // Umbrales simples para el MVP. En una iteracion futura esto se mueve
    // a configuracion (application.properties) en vez de estar hardcoded.
    private static final int CPU_THRESHOLD = 80;
    private static final int MEMORY_THRESHOLD = 85; //85;

    @Inject
    com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Inject
    MeterRegistry registry;

    @Transactional
    public void evaluate(NetworkMetricEvent event) {
        if (event.cpu() > CPU_THRESHOLD) {
            raiseAlert("HIGH", "HIGH_CPU", event.device(),
                    "CPU al " + event.cpu() + "%, supera el umbral de " + CPU_THRESHOLD + "%");
        }
        if (event.memory() > MEMORY_THRESHOLD) {
            raiseAlert("MEDIUM", "HIGH_MEMORY", event.device(),
                    "Memoria al " + event.memory() + "%, supera el umbral de " + MEMORY_THRESHOLD + "%");
        }
    }

    private void raiseAlert(String severity, String eventType, String device, String message) {
        AlertEntity alert = new AlertEntity();
        alert.severity = severity;
        alert.event = eventType;
        alert.sourceDevice = device;
        alert.message = message;
        alert.timestamp = Instant.now();
        alert.persist();
        registry.counter("cyberstream_alerts_total", "severity", severity, "event", eventType).increment();
        try {
            var payload = objectMapper.createObjectNode();
            payload.put("type", "alert");
            payload.put("severity", severity);
            payload.put("event", eventType);
            payload.put("sourceDevice", device);
            payload.put("message", message);
            MetricsWebSocket.broadcast(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            Log.error("error transmitiendo alerta por websocket", e);
        }
        Log.warnf("ALERTA [%s] %s en %s: %s", severity, eventType, device, message);
    }
}
