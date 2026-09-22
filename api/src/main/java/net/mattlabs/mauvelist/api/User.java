package net.mattlabs.mauvelist.api;

import java.util.UUID;

public class User {

    private final UUID minecraftUUID;
    private String minecraftUsername, discordUserID;
    private final boolean preexisting;

    public User(UUID minecraftUUID, String minecraftUsername, String discordUserID, boolean preexisting) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftUsername = minecraftUsername;
        this.discordUserID = discordUserID;
        this.preexisting = preexisting;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public String getDiscordUserID() {
        return discordUserID;
    }

    public boolean isPreexisting() {
        return preexisting;
    }
}
