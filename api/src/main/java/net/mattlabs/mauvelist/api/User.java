package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class User {

    private final long id;
    private final Instant createdAt;
    private final boolean preexisting;
    private List<MinecraftAccount> minecraftAccounts;
    private DiscordAccount discordAccount;

    public User(long id, Instant createdAt, boolean preexisting) {
        this.id = id;
        this.createdAt = createdAt;
        this.preexisting = preexisting;
        minecraftAccounts = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isPreexisting() {
        return preexisting;
    }

    public List<MinecraftAccount> getMinecraftAccounts() {
        return minecraftAccounts;
    }

    public DiscordAccount getDiscordAccount() {
        return discordAccount;
    }

    public void setMinecraftAccounts(List<MinecraftAccount> minecraftAccounts) {
        this.minecraftAccounts = minecraftAccounts;
    }

    public void setDiscordAccount(DiscordAccount discordAccount) {
        this.discordAccount = discordAccount;
    }
}
