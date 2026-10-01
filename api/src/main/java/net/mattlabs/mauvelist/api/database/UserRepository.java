package net.mattlabs.mauvelist.api.database;

import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.api.User;

import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.UUID;

public class UserRepository {

    private final DatabaseManager databaseManager;
    private final Calendar utcCalendar;

    public UserRepository() {
        databaseManager = MauveListAPI.getInstance().getDatabaseManager();
        utcCalendar = databaseManager.utcCalendar();
    }

    public User findUserByUUID(UUID uuid) {
        String query = """
                SELECT minecraft_uuid, minecraft_username, discord_user_id, created_at, last_seen_at, preexisting
                FROM users
                WHERE minecraft_uuid = ?
                """;

        // Get connection and create query
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // Set UUID in query string
            statement.setString(1, uuid.toString());

            // Execute
            try (ResultSet result = statement.executeQuery()) {
                // Return user object if found, null otherwise
                if (result.next()) {
                    return new User(
                            UUID.fromString(result.getString("minecraft_uuid")),
                            result.getString("minecraft_username"),
                            result.getObject("discord_user_id", Long.class),
                            result.getTimestamp("created_at", utcCalendar).toInstant(),
                            result.getTimestamp("last_seen_at") == null ? null : result.getTimestamp("last_seen_at", utcCalendar).toInstant(),
                            result.getBoolean("preexisting")
                    );
                }
                else return null;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to find user by UUID", e);
        }
    }

    public void createUser(User user) {
        String query = """
                INSERT INTO users (minecraft_uuid, minecraft_username, last_seen_at)
                VALUES (?, ?, ?)
        """;

        // Get connection and create query
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // Set UUID in query string
            statement.setString(1, user.getMinecraftUUID().toString());
            statement.setString(2, user.getMinecraftUsername());
            statement.setTimestamp(3, Timestamp.from(user.getLastSeenAt()), utcCalendar);

            // Execute
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create new user", e);
        }
    }

    public void updateLastSeen(UUID uuid, Instant lastSeenAt) {
        String query = """
                UPDATE users
                SET last_seen_at = ?
                WHERE minecraft_uuid = ?
                AND (last_seen_at IS NULL OR last_seen_at < ?)
                """;

        // Get connection and create query
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // Set timestamp and UUID in query string
            statement.setTimestamp(1, Timestamp.from(lastSeenAt), utcCalendar);
            statement.setString(2, uuid.toString());
            statement.setTimestamp(3, Timestamp.from(lastSeenAt), utcCalendar);

            // Execute
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to update last seen timestamp for user " + uuid, e);
        }
    }
}
