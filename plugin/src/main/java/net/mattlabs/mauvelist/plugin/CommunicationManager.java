package net.mattlabs.mauvelist.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.logging.Logger;

public class CommunicationManager {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper mapper;
    private final Logger logger;

    public CommunicationManager(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        logger = MauveList.getInstance().getLogger();
        logger.info("REST API URL: " + baseUrl);
    }

    public void playerActivity(UUID uuid, String name) {
        try {
            String json = mapper.writeValueAsString(new PlayerActivityRequest(name));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/users/" + uuid + "/activity"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        logger.info(response.body());
                    });
        }
        catch (JsonProcessingException e) {
            logger.severe("Error processing JSON for request: " + e.getMessage());
        }
    }
}
