package net.mattlabs.mauvelist.api;

import net.mattlabs.mauvelist.api.database.UserRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

public class UserManager {

    private final UserRepository userRepository;
    private final Logger logger;

    public UserManager() {
        userRepository = new UserRepository();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public User getUserFromMinecraftUUID(UUID uuid) {
        return userRepository.findUserByMinecraftUUID(uuid);
    }

    public User getUserFromDiscordUserID(long discordUserID) {
        return userRepository.findUserByDiscordUserID(discordUserID);
    }

    public void updateLastSeenOnMinecraft(UUID uuid, String username, Instant lastSeenAt, boolean preexisting) {
        // Check if Minecraft account exists and update, if not, create
        if (userRepository.findUserByMinecraftUUID(uuid) != null) {
            userRepository.updateLastSeenOnMinecraft(uuid, lastSeenAt);
        }
        else {
            logger.info("User linked to UUID " + uuid + " not found, creating new user.");
            userRepository.createUserFromMinecraftAccount(uuid, username, lastSeenAt, preexisting);
        }
    }

    public void updateLastSeenOnDiscord(long discordUserID, String username, Instant lastSeenAt, boolean preexisting) {
        // Check if Discord account exists and update, if not, create
        if (userRepository.findUserByDiscordUserID(discordUserID) != null) {
            userRepository.updateLastSeenOnDiscord(discordUserID, lastSeenAt);
        }
        else {
            logger.info("User linked to Discord user ID " + discordUserID + " not found, creating new user.");
            userRepository.createUserFromDiscordAccount(discordUserID, username, lastSeenAt, preexisting);
        }
    }
}
