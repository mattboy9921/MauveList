package net.mattlabs.mauvelist.api;

import java.util.UUID;

public class UserManager {

    private final UserRepository userRepository;

    public UserManager() {
        userRepository = new UserRepository();
    }

    public User getUser(UUID uuid) {
        return userRepository.findUserByUUID(uuid);
    }
}
