package net.mattlabs.mauvelist.plugin.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.common.ConfigurateFormat;
import net.mattlabs.mauvelist.common.ConfigurateManager;
import net.mattlabs.mauvelist.common.records.NotificationType;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;
import net.mattlabs.mauvelist.common.records.WebSocketNotification;
import net.mattlabs.mauvelist.plugin.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class CommunicationManager {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper mapper;
    private final Logger logger;
    private ConfigurateManager configurateManager;
    private CommunicationQueue queue;
    private volatile HealthStatus healthStatus;
    private BukkitTask healthCheck;
    private volatile WebSocket webSocket;
    private volatile Instant lastWebSocketPing, lastWebSocketResponse;
    private final AtomicBoolean webSocketConnecting, synchronizing;

    public CommunicationManager(String baseUrl) {
        logger = MauveList.getInstance().getLogger();
        logger.info("Initializing communication...");

        // HTTP
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();

        // JSON mapping with ISO time format
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ensure methods only run one instance at a time
        webSocketConnecting = new AtomicBoolean(false);
        synchronizing = new AtomicBoolean(false);

        // Communication Queue
        initializeQueue();

        // Health check
        initializeHealthCheck();

        logger.info("Communication initialized!");
    }

    public void playerActivity(UUID uuid, String name) {
        try {
            String url = baseUrl + "/api/v1/users/" + uuid + "/activity";
            String json = mapper.writeValueAsString(new PlayerActivityRequest(name, Instant.now()));

            sendPostRequest(url, json);
        }
        catch (JsonProcessingException e) {
            logger.severe("Error processing JSON for request: " + e.getMessage());
        }
    }

    private void initializeQueue() {
        // Get Configurate manager
        configurateManager = MauveList.getInstance().getConfigurateManager();

        // Add JSON file with concurrent linked queue serializer
        configurateManager.add(
                "communication_queue.json",
                TypeToken.get(CommunicationQueue.class),
                new CommunicationQueue(),
                CommunicationQueue::new,
                ConfigurateFormat.JSON,
                opts -> opts.serializers(build -> build.register(
                        type -> type instanceof ParameterizedType parameterizedType
                                && parameterizedType.getRawType() == ConcurrentLinkedQueue.class,
                        ConcurrentLinkedQueueSerializer.INSTANCE)
                )
        );

        // Try to save default values
        if (!configurateManager.saveDefaults("communication_queue.json")) throw new IllegalStateException("Failed to save default communication queue");

        // Load file
        configurateManager.load("communication_queue.json");

        // Save/update file
        configurateManager.save("communication_queue.json");

        queue = configurateManager.get("communication_queue.json");
    }

    private void addRequestToQueue(String url, RequestType requestType, String body) {
        queue.getQueuedRequests().add(new QueuedRequest(url, requestType, body));
        saveQueue();
    }

    private void saveQueue() {
        configurateManager.save("communication_queue.json");
    }

    private void initializeHealthCheck() {
        String url = baseUrl + "/api/v1/health";

        healthStatus = HealthStatus.UNKNOWN;

        healthCheck = Bukkit.getScheduler().runTaskTimerAsynchronously(MauveList.getInstance(), () -> {
            sendHealthCheckRequest(url)
                    .thenAccept(success -> {
                        if (success && webSocketPing()) {
                            if (healthStatus != HealthStatus.HEALTHY) {
                                healthStatus = HealthStatus.HEALTHY;
                                logger.info("Connection Health - Successfully connected to MauveList API");
                                synchronize();
                            }
                        }
                        // Bad response
                        else {
                            if (healthStatus != HealthStatus.UNHEALTHY) {
                                healthStatus = HealthStatus.UNHEALTHY;
                                logger.warning("Connection Health - Failed to communicate with MauveList API");
                            }
                        }
                    });
        }, 0, 20);
    }

    private void synchronize() {
        if (synchronizing.compareAndSet(false, true)) {
            logger.info("Initializing API sync...");

            if (!queue.getQueuedRequests().isEmpty()) {
                logger.info("Processing " + queue.getQueuedRequests().size() + " queued requests...");
            }

            // Recursively process requests
            processNextQueuedRequest()
                    .thenRun(() -> logger.info("API sync completed!"))
                    .whenComplete((result, error) -> synchronizing.set(false));
        }
    }

    private CompletableFuture<Void> processNextQueuedRequest() {
        // Grab next request from queue
        QueuedRequest request = queue.getQueuedRequests().peek();

        if (request != null) {
            return sendQueuedRequest(request.url(), request.requestType(), request.body())
                    .thenCompose(success -> {
                        // Request failed
                        if (!success) return CompletableFuture.completedFuture(null);

                        // Request succeeded, remove it from the queue
                        queue.getQueuedRequests().poll();
                        saveQueue();

                        return processNextQueuedRequest();
                    });
        }
        else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Boolean> sendGetRequest(String url) {
        return sendRequest(url, RequestType.GET, null, true);
    }

    private CompletableFuture<Boolean> sendPostRequest(String url, String body) {
        return sendRequest(url, RequestType.POST, body, true);
    }

    private CompletableFuture<Boolean> sendHealthCheckRequest(String url) {
        return sendRequest(url, RequestType.GET, null, false);
    }

    private CompletableFuture<Boolean> sendQueuedRequest(String url, RequestType requestType, String body) {
        return sendRequest(url, requestType, body, false);
    }

    private CompletableFuture<Boolean> sendRequest(String url, RequestType requestType, String body, boolean queueOnFailure) {
        // Build request
        HttpRequest request = switch (requestType) {
            case GET -> HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            case POST -> HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
        };

        // Check API health status, only notify failure when API healthy
        boolean healthy = healthStatus == HealthStatus.HEALTHY;

        // Try to send the request and return the result
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) return true;
                    else {
                        if (healthy) logger.warning("Received response from MauveList API: " + response.body());
                        if (queueOnFailure && requestType == RequestType.POST) addRequestToQueue(url, requestType, body);
                        return false;
                    }

                })
                .exceptionally(throwable -> {
                    if (healthy) logger.warning("Failed to communicate with MauveList API: " + throwable.getMessage());
                    if (queueOnFailure && requestType == RequestType.POST) addRequestToQueue(url, requestType, body);
                    return false;
                });
    }

    public void shutdown() {
        logger.info("Shutting down communications...");

        if (webSocket != null && !webSocket.isInputClosed() && !webSocket.isOutputClosed())
            webSocket.sendClose(1000, "MauveList plugin shutting down");
        healthCheck.cancel();
        saveQueue();

        logger.info("Communications shutdown complete!");
    }

    private void connectWebSocket() {
        if (webSocketConnecting.compareAndSet(false, true)) {
            String url = baseUrl.replaceFirst("^http", "ws") + "/api/v1/notifications";

            // Check API health status, only notify failure when API healthy
            boolean healthy = healthStatus == HealthStatus.HEALTHY;

            WebSocket.Listener listener = new WebSocket.Listener() {

                private final StringBuilder textBuffer = new StringBuilder();

                @Override
                public void onOpen(WebSocket webSocket) {
                    CommunicationManager.this.webSocket = webSocket;
                    lastWebSocketPing = Instant.now();
                    lastWebSocketResponse = Instant.now();

                    logger.info("Connected to MauveList API WebSocket!");

                    webSocket.request(1);
                }

                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    lastWebSocketResponse = Instant.now();

                    // Build message
                    textBuffer.append(data);

                    // Process complete message
                    if (last) {
                        try {
                            WebSocketNotification notification = mapper.readValue(data.toString(), WebSocketNotification.class);

                            if (notification.notificationType() == NotificationType.CHANGES_AVAILABLE) {
                                logger.info("MauveList API notified changes are available!");
                                synchronize();
                            }
                        }
                        catch (JsonProcessingException e) {
                            logger.warning("Error parsing WebSocket JSON: " + e.getMessage());
                        }
                        finally {
                            textBuffer.setLength(0);
                        }
                    }

                    webSocket.request(1);

                    return null;
                }

                @Override
                public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                    logger.warning("MauveList API WebSocket disconnected: " + statusCode + " " + reason);

                    CommunicationManager.this.webSocket = null;

                    return null;
                }

                @Override
                public void onError(WebSocket webSocket, Throwable error) {
                    if (healthy) logger.warning("MauveList API WebSocket error: " + error.getMessage());

                    CommunicationManager.this.webSocket = null;
                }

                @Override
                public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer data) {
                    lastWebSocketResponse = Instant.now();

                    webSocket.request(1);

                    return null;
                }
            };

            httpClient.newWebSocketBuilder().buildAsync(URI.create(url), listener)
                    .whenComplete((result, error) -> {
                        webSocketConnecting.set(false);

                        if (error != null && healthy) {
                            logger.warning("MauveList API WebSocket error: " + error.getMessage());
                        }
                    });
        }
    }

    public boolean webSocketPing() {
        // No connected WebSocket
        if (webSocket == null || webSocket.isInputClosed() || webSocket.isOutputClosed()) {
            connectWebSocket();
            return false;
        }

        // Only ping every 15 seconds
        if (Duration.between(lastWebSocketPing, Instant.now()).getSeconds() >= 15) {
            lastWebSocketPing = Instant.now();

            webSocket.sendPing(ByteBuffer.wrap(new byte[]{1}));
        }

        return Duration.between(lastWebSocketResponse, Instant.now()).getSeconds() < 30;
    }
}
