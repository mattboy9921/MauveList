package net.mattlabs.mauvelist.api.communication;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class CommunicationManager {

    private final Javalin app;
    private final UserController userController;
    private final NotificationManager notificationManager;

    public CommunicationManager() {
        userController = new UserController();
        notificationManager = new NotificationManager();

        app = Javalin.create(config -> {
            // JSON Mapping with ISO time format
            JavalinJackson jackson = new JavalinJackson();
            jackson.getMapper().registerModule(new JavaTimeModule());
            jackson.getMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            config.jsonMapper(jackson);

            // Routes
            config.routes.get("/api/v1/health", new HealthController()::getHealth);
            config.routes.get("/api/v1/users/{uuid}", userController::getUser);
            config.routes.post("/api/v1/users/{uuid}/activity", userController::playerActivity);

            config.routes.ws("/api/v1/notifications", ws -> {
                ws.onConnect(notificationManager::connect);
                ws.onClose(notificationManager::disconnect);
                ws.onError(notificationManager::error);
            });
        });
    }

    public void start() {
        app.start(8080);
    }

    public void stop() {
        app.stop();
    }

    public void notifyChangesAvailable() {
        notificationManager.notifyChangesAvailable();
    }
}
