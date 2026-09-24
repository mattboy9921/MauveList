package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID minecraftUUID,
        String minecraftUsername,
        Long discordUserId,
        Instant createdAt,
        Instant lastSeenAt,
        boolean preexisting
) {
}
