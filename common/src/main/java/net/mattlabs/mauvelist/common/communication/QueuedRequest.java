package net.mattlabs.mauvelist.common.communication;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record QueuedRequest(
        String url,
        RequestType requestType,
        String body
) {}
