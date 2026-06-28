package com.cyberstream;

import io.quarkus.logging.Log;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/live")
@ApplicationScoped
public class MetricsWebSocket {

    // Set de sesiones conectadas. Cualquier mensaje que llegue (metricas o
    // alertas) se reenvia a todas. Para un MVP esto es suficiente; en una
    // version con mas carga esto se reemplazaria por un pub/sub real.
    private static final Set<Session> SESSIONS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        Log.infof("WebSocket cliente conectado: %s (total=%d)", session.getId(), SESSIONS.size());
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        Log.infof("WebSocket cliente desconectado: %s (total=%d)", session.getId(), SESSIONS.size());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        Log.errorf(throwable, "error en WebSocket sesion %s", session.getId());
        SESSIONS.remove(session);
    }

    /** Llamado desde NetworkMetricConsumer y DetectionRules para difundir en vivo. */
    public static void broadcast(String jsonPayload) {
        for (Session session : SESSIONS) {
            session.getAsyncRemote().sendText(jsonPayload);
        }
    }
}
