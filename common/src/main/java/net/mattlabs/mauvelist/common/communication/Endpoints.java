package net.mattlabs.mauvelist.common.communication;

import java.util.UUID;

public final class Endpoints {

    private static final String basePath = "/api/v1";

    private  Endpoints() {
    }

    public static String minecraftActivity(UUID minecraftUUID) {
        return basePath + "/users/minecraft/" + minecraftUUID + "/activity";
    }

    public static String discordActivity(long discordUserID) {
        return basePath + "/users/discord/" + discordUserID + "/activity";
    }
}
