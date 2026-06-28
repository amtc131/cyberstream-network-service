package com.cyberstream;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.MeterRegistry;

@ApplicationScoped
public class NetworkMetricConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    DetectionRules detectionRules;

    @Inject
    MeterRegistry registry;

    @Incoming("network-metrics")
    @Blocking
    @Transactional
    public void consume(String message) {
        registry.counter("cyberstream_metrics_processed_total").increment();
        try {
            NetworkMetricEvent event = objectMapper.readValue(message, NetworkMetricEvent.class);

            NetworkMetricEntity entity = new NetworkMetricEntity();
            entity.device = event.device();
            entity.timestamp = event.timestamp();
            entity.cpu = event.cpu();
            entity.memory = event.memory();
            entity.wanMbps = event.wanMbps();
            entity.vpn = event.vpn();
            entity.connectedClients = event.connectedClients();
            entity.latencyMs = event.latencyMs();
            entity.persist();
            detectionRules.evaluate(event);
            ObjectNode wsPayload = objectMapper.createObjectNode();
            wsPayload.put("type", "metric");
            wsPayload.set("data", objectMapper.valueToTree(event));
            MetricsWebSocket.broadcast(objectMapper.writeValueAsString(wsPayload));

            Log.infof("metrica persistida: device=%s cpu=%d mem=%d wanMbps=%.2f",
                    event.device(), event.cpu(), event.memory(), event.wanMbps());
        } catch (Exception e) {
            Log.errorf(e, "error procesando mensaje de network.metrics: %s", message);
        }
    }
}
