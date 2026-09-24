package net.mattlabs.mauvelist.api;

import java.time.Instant;
import java.util.UUID;

public class UserRepository {

    public User findUserByUUID(UUID uuid) {
        return new User(
                uuid,
                "minecraftUsername",
                1,
                Instant.now(),
                false
        );
    }
}
