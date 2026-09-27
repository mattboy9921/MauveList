package net.mattlabs.mauvelist.common.records;

import java.time.Instant;

public record PlayerActivityRequest(
        String minecraftUsername,
        Instant occurredAt
) {}
