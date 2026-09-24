package net.mattlabs.mauvelist.api.communication;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class CommunicationManager {

    private final Javalin app;

    public CommunicationManager() {
        app = Javalin.create(config -> {
            // JSON Mapping with ISO time format
            JavalinJackson jackson = new JavalinJackson();
            jackson.getMapper().registerModule(new JavaTimeModule());
            jackson.getMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            config.jsonMapper(jackson);

            // Routes
            config.routes.get("/health", new HealthController()::getHealth);
            config.routes.get("/api/v1/users/{uuid}", new UserController()::getUser);
        });
    }

    public void start() {
        app.start(8080);
    }

    public void stop() {
        app.stop();
    }
}
