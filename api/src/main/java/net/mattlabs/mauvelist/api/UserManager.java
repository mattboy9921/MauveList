package net.mattlabs.mauvelist.api;

import java.util.UUID;
import java.util.logging.Logger;

public class UserManager {

    private final UserRepository userRepository;
    private final Logger logger;

    public UserManager() {
        userRepository = new UserRepository();
        logger = MauveListAPI.getInstance().getLogger();
    }

    public User getUser(UUID uuid) {
        return userRepository.findUserByUUID(uuid);
    }

    public void updateLastSeen(User user) {
        // Check if user exists and update, if not, create
        if (userRepository.findUserByUUID(user.getMinecraftUUID()) != null) {
            userRepository.updateLastSeen(user.getMinecraftUUID(), user.getLastSeenAt());
        }
        else {
            logger.info("User " + user.getMinecraftUUID() + " not found, creating new user.");
            userRepository.createUser(user);
        }
    }
}
