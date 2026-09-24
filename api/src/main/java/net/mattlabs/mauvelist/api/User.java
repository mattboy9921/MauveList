package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID minecraftUUID;
    private String minecraftUsername;
    private long discordUserID;
    private final Instant createdAt;
    private final boolean preexisting;

    public User(UUID minecraftUUID, String minecraftUsername, long discordUserID, Instant createdAt, boolean preexisting) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftUsername = minecraftUsername;
        this.discordUserID = discordUserID;
        this.createdAt = createdAt;
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

    public boolean isPreexisting() {
        return preexisting;
    }

    public void setMinecraftUsername(String minecraftUsername) {
        this.minecraftUsername = minecraftUsername;
    }

    public void setDiscordUserID(long discordUserID) {
        this.discordUserID = discordUserID;
    }
}
