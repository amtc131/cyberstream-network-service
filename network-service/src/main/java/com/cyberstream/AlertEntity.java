package com.cyberstream;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.Instant;

@Entity
public class AlertEntity extends PanacheEntity {
    public String severity;
    public String event;
    public String sourceDevice;
    public String message;
    public Instant timestamp;
}
