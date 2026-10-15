package net.mattlabs.mauvelist.api;

import java.time.Instant;

public class DiscordAccount {

    private final long userID;
    private final long discordUserID;
    private final String discordUsername;
    private final Instant lastSeenAt;

    public DiscordAccount(long userID, long discordUserID, String discordUsername, Instant lastSeenAt) {
        this.userID = userID;
        this.discordUserID = discordUserID;
        this.discordUsername = discordUsername;
        this.lastSeenAt = lastSeenAt;
    }

    public long getUserID() {
        return userID;
    }

    public long getDiscordUserID() {
        return discordUserID;
    }

    public String getDiscordUsername() {
        return discordUsername;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }
}
