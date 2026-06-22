package com.cyberstream;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.Instant;

@Entity
public class NetworkMetricEntity extends PanacheEntity {
    public String device;
    public Instant timestamp;
    public int cpu;
    public int memory;
    public double wanMbps;
    public String vpn;
    public Integer connectedClients;
    public Double latencyMs;
}
