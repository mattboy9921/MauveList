package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;
import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.api.User;
import net.mattlabs.mauvelist.api.UserManager;
import net.mattlabs.mauvelist.api.UserResponse;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

public class UserController {

    private final UserManager userManager;
    private Logger logger;

    public UserController() {
        userManager = new UserManager();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public void getUser(Context context) {
        UUID uuid = UUID.fromString(context.pathParam("uuid"));

        User user = userManager.getUser(uuid);

        UserResponse response = new UserResponse(
                user.getMinecraftUUID(),
                user.getMinecraftUsername(),
                user.getDiscordUserID(),
                user.getCreatedAt(),
                user.getLastSeenAt(),
                user.isPreexisting()
        );

        context.json(response);
    }

    public void playerActivity(Context context) {
        UUID uuid = UUID.fromString(context.pathParam("uuid"));
        PlayerActivityRequest request = context.bodyAsClass(PlayerActivityRequest.class);

        UserResponse response = new UserResponse(
                uuid,
                request.minecraftUsername(),
                1L,
                Instant.now(),
                Instant.now(),
                false
        );

        context.json(response);

        logger.info("Received player activity request: " + response);
    }
}
