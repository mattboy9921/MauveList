package net.mattlabs.mauvelist.common.records;

import java.time.Instant;

public record MinecraftActivityRequest(
        String minecraftUsername,
        Instant occurredAt
) {}
