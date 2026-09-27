package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;
import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.api.User;
import net.mattlabs.mauvelist.api.UserManager;
import net.mattlabs.mauvelist.api.UserResponse;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;

import java.util.UUID;
import java.util.logging.Logger;

public class UserController {

    private final UserManager userManager;
    private final Logger logger;

    public UserController() {
        userManager = new UserManager();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public void getUser(Context context) {
        UUID uuid = UUID.fromString(context.pathParam("uuid"));

        User user = userManager.getUser(uuid);

        if (user != null) {
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
        else {
            context.status(404);
        }
    }

    public void playerActivity(Context context) {
        UUID uuid = UUID.fromString(context.pathParam("uuid"));
        PlayerActivityRequest request = context.bodyAsClass(PlayerActivityRequest.class);

        logger.info("Received player activity request for user " + request.minecraftUsername() + " (" + uuid + ").");

        User user = new User(uuid, request.minecraftUsername(), null, null, request.occurredAt(), false);

        userManager.updateLastSeen(user);

        context.status(204);
    }
}
