package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID minecraftUUID;
    private String minecraftUsername;
    private long discordUserID;
    private final Instant createdAt;
    private Instant lastSeenAt;
    private final boolean preexisting;

    public User(UUID minecraftUUID, String minecraftUsername, long discordUserID, Instant createdAt, Instant lastSeenAt, boolean preexisting) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftUsername = minecraftUsername;
        this.discordUserID = discordUserID;
        this.createdAt = createdAt;
        this.lastSeenAt = lastSeenAt;
        this.preexisting = preexisting;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public long getDiscordUserID() {
        return discordUserID;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public boolean isPreexisting() {
        return preexisting;
    }

    public void setMinecraftUsername(String minecraftUsername) {
        this.minecraftUsername = minecraftUsername;
    }

    public void setDiscordUserID(long discordUserID) {
        this.discordUserID = discordUserID;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
