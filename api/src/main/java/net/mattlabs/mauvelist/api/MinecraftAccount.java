package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.UUID;

public class MinecraftAccount {

    private final long userID;
    private final UUID uuid;
    private final String username;
    private final Instant lastSeenAt;

    public MinecraftAccount(long userID, UUID uuid, String username, Instant lastSeenAt) {
        this.userID = userID;
        this.uuid = uuid;
        this.username = username;
        this.lastSeenAt = lastSeenAt;
    }

    public long getUserID() {
        return userID;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }
}
