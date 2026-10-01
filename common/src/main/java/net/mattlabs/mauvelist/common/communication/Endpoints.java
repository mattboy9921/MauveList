package net.mattlabs.mauvelist.common.communication;

import java.util.UUID;

public final class Endpoints {

    private static final String basePath = "/api/v1";

    private  Endpoints() {
    }

    public static String playerActivity(UUID minecraftUUID) {
        return basePath + "/users/" + minecraftUUID + "/activity";
    }
}
