package com.cyberstream;

import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api")
public class NetworkApiResource {

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "pong"; // endpoint publico, sin auth, solo para verificar que el servicio responde
    }

    @GET
    @Path("/metrics/latest")
    @RolesAllowed("viewer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NetworkMetricEntity> latestMetrics() {
        return NetworkMetricEntity.findAll(Sort.descending("id")).page(0, 20).list();
    }

    @GET
    @Path("/alerts")
    @RolesAllowed("viewer")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AlertEntity> latestAlerts() {
        return AlertEntity.findAll(Sort.descending("id")).page(0, 20).list();
    }
}
