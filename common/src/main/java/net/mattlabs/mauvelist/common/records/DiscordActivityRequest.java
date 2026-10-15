package net.mattlabs.mauvelist.common.records;

import java.time.Instant;

public record DiscordActivityRequest(
        String discordUsername,
        Instant occurredAt
) {}
