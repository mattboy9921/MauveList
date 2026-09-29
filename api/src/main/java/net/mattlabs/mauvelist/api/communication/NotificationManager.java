package net.mattlabs.mauvelist.api.communication;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import net.mattlabs.mauvelist.api.MauveListAPI;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class NotificationManager {

    private final Logger logger;
    private final Set<WsContext> connections;

    public NotificationManager() {
        logger = MauveListAPI.getInstance().getLogger();
        connections = ConcurrentHashMap.newKeySet();
    }

    public void connect(WsContext context) {
        logger.info("WebSocket connection connected: " + context.host());
        connections.add(context);
    }

    public void disconnect(WsContext context) {
        logger.info("WebSocket connection disconnected: " + context.host());
        connections.remove(context);
    }

    public void error(WsErrorContext context) {
        logger.info("WebSocket connection error: " + context.error().getMessage());
        connections.remove(context);
    }

    public void notifyChangesAvailable() {
        logger.info("Notifying " + connections.size() + " WebSocket client" + (connections.size() != 1 ? "s" : "") + " changes available...");
        for (WsContext connection : connections) {
            connection.send("changes_available");
        }
    }
}
