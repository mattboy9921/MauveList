package net.mattlabs.mauvelist.api.communication;

import io.javalin.http.Context;
import net.mattlabs.mauvelist.api.User;
import net.mattlabs.mauvelist.api.UserManager;
import net.mattlabs.mauvelist.api.UserResponse;

import java.util.UUID;

public class UserController {

    private final UserManager userManager;

    public UserController() {
        userManager = new UserManager();
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
}
