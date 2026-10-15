package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Instant createdAt,
        boolean preexisting,
        List<MinecraftAccount> minecraftAccounts,
        DiscordAccount discordAccount
) {
}
