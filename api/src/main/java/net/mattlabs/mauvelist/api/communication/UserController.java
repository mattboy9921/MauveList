package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;
import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.api.User;
import net.mattlabs.mauvelist.api.UserManager;
import net.mattlabs.mauvelist.api.UserResponse;
import net.mattlabs.mauvelist.common.records.DiscordActivityRequest;
import net.mattlabs.mauvelist.common.records.MinecraftActivityRequest;

import java.util.UUID;
import java.util.logging.Logger;

public class UserController {

    private final UserManager userManager;
    private final Logger logger;

    public UserController() {
        userManager = new UserManager();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public void getUserFromMinecraftUUID(Context context) {
        UUID uuid = context.pathParamAsClass("minecraftUUID", UUID.class).get();

        User user = userManager.getUserFromMinecraftUUID(uuid);

        respondWithUser(context, user);
    }

    public void getUserFromDiscordUserID(Context context) {
        long discordUserID = context.pathParamAsClass("discordUserID", Long.class).get();

        User user = userManager.getUserFromDiscordUserID(discordUserID);

        respondWithUser(context, user);
    }

    private void respondWithUser(Context context, User user) {
        if (user != null) {
            UserResponse response = new UserResponse(
                    user.getCreatedAt(),
                    user.isPreexisting(),
                    user.getMinecraftAccounts(),
                    user.getDiscordAccount()
            );

            context.json(response);
        }
        else {
            context.status(404);
        }
    }

    public void minecraftActivity(Context context) {
        UUID uuid = context.pathParamAsClass("minecraftUUID", UUID.class).get();
        MinecraftActivityRequest request = context.bodyAsClass(MinecraftActivityRequest.class);

        logger.info("Received Minecraft activity request for user " + request.minecraftUsername() + " (" + uuid + ").");

        userManager.updateLastSeenOnMinecraft(uuid, request.minecraftUsername(), request.occurredAt(), false);

        context.status(204);
    }

    public void discordActivity(Context context) {
        long discordUserID = context.pathParamAsClass("discordUserID",  Long.class).get();
        DiscordActivityRequest request = context.bodyAsClass(DiscordActivityRequest.class);

        logger.info("Received Discord activity request for user " + request.discordUsername() + " (" + discordUserID + ").");

        userManager.updateLastSeenOnDiscord(discordUserID, request.discordUsername(), request.occurredAt(), false);

        context.status(204);
    }
}
