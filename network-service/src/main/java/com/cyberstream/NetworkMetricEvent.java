package com.cyberstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record NetworkMetricEvent(
        String device,
        Instant timestamp,
        int cpu,
        int memory,
        @JsonProperty("wanMbps") double wanMbps,
        String vpn,
        @JsonProperty("connectedClients") Integer connectedClients,
        @JsonProperty("latencyMs") Double latencyMs
) {
}
