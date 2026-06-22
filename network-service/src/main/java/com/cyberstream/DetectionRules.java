package com.cyberstream;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;

@ApplicationScoped
public class DetectionRules {

    // Umbrales simples para el MVP. En una iteracion futura esto se mueve
    // a configuracion (application.properties) en vez de estar hardcoded.
    private static final int CPU_THRESHOLD = 80;
    private static final int MEMORY_THRESHOLD = 15; //85;

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

        Log.warnf("ALERTA [%s] %s en %s: %s", severity, eventType, device, message);
    }
}
