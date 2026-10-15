package net.mattlabs.mauvelist.api.database;

import net.mattlabs.mauvelist.api.DiscordAccount;
import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.api.MinecraftAccount;
import net.mattlabs.mauvelist.api.User;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class UserRepository {

    private final DatabaseManager databaseManager;
    private final Calendar utcCalendar;

    public UserRepository() {
        databaseManager = MauveListAPI.getInstance().getDatabaseManager();
        utcCalendar = databaseManager.utcCalendar();
    }

    public User findUserByMinecraftUUID(UUID uuid) {
        String query = """
                SELECT
                    u.id,
                    u.created_at,
                    u.preexisting
                FROM users u
                JOIN minecraft_accounts ma ON ma.user_id = u.id
                WHERE ma.minecraft_uuid = ?
                """;

        // Get connection and create query
        try (Connection connection = databaseManager.getConnection()) {
            // Don't commit changes until all queries succeed
            connection.setAutoCommit(false);

            try {
                User user = null;

                // Prepare statement
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    // Set UUID in query string
                    statement.setString(1, uuid.toString());

                    // Execute
                    try (ResultSet result = statement.executeQuery()) {
                        // Populate user
                        if (result.next()) {
                            user = new User(
                                    result.getLong("id"),
                                    result.getTimestamp("created_at", utcCalendar).toInstant(),
                                    result.getBoolean("preexisting")
                            );
                        }
                    }
                }

                if (user != null) {
                    user.setMinecraftAccounts(findMinecraftAccountsByUserID(connection, user.getId()));
                    user.setDiscordAccount(findDiscordAccountByUserID(connection, user.getId()));
                }

                connection.commit();
                return user;
            }
            catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to find user by UUID", e);
        }
    }

    public User findUserByDiscordUserID(long discordUserID) {
        String query = """
                SELECT
                    u.id,
                    u.created_at,
                    u.preexisting
                FROM users u
                JOIN discord_accounts da ON da.user_id = u.id
                WHERE da.discord_user_id = ?
                """;

        // Get connection and create query
        try (Connection connection = databaseManager.getConnection()) {
            // Don't commit changes until all queries succeed
            connection.setAutoCommit(false);

            try {
                User user = null;

                // Prepare statement
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    // Set Discord user ID in query string
                    statement.setLong(1, discordUserID);

                    // Execute
                    try ( ResultSet result = statement.executeQuery()) {
                        // Populate user
                        if (result.next()) {
                            user = new User(
                                    result.getLong("id"),
                                    result.getTimestamp("created_at", utcCalendar).toInstant(),
                                    result.getBoolean("preexisting")
                            );
                        }
                    }
                }

                if (user != null) {
                    user.setMinecraftAccounts(findMinecraftAccountsByUserID(connection, user.getId()));
                    user.setDiscordAccount(findDiscordAccountByUserID(connection, user.getId()));
                }

                connection.commit();
                return user;
            }
            catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to find user by Discord user ID", e);
        }
    }

    private List<MinecraftAccount> findMinecraftAccountsByUserID(Connection connection, long userID) throws SQLException {
        List<MinecraftAccount> accounts = new ArrayList<>();

        String query = """
                SELECT
                    minecraft_uuid,
                    minecraft_username,
                    last_seen_at
                FROM minecraft_accounts
                WHERE user_id = ?
                """;

        // Get connection and create query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set user ID in query string
            statement.setLong(1, userID);

            // Execute
            try (ResultSet result = statement.executeQuery()) {
                // Populate accounts
                while (result.next()) {
                    Timestamp timestamp = result.getTimestamp("last_seen_at", utcCalendar);
                    Instant lastSeenAt = timestamp != null ? timestamp.toInstant() : null;

                    accounts.add(new MinecraftAccount(
                            userID,
                            UUID.fromString(result.getString("minecraft_uuid")),
                            result.getString("minecraft_username"),
                            lastSeenAt
                    ));
                }
            }
        }

        return accounts;
    }

    private DiscordAccount findDiscordAccountByUserID(Connection connection, long userID) throws SQLException {
        String query = """
                SELECT
                    discord_user_id,
                    discord_username,
                    last_seen_at
                FROM discord_accounts
                WHERE user_id = ?
                """;

        // Get connection and create query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set user ID in query string
            statement.setLong(1, userID);

            // Execute
            try (ResultSet result = statement.executeQuery()) {
                // Populate accounts
                if (result.next()) {
                    return new DiscordAccount(
                            userID,
                            result.getLong("discord_user_id"),
                            result.getString("discord_username"),
                            result.getTimestamp("last_seen_at", utcCalendar).toInstant()
                    );
                }
                else return null;
            }
        }
    }

    public void createUserFromMinecraftAccount(UUID uuid, String username, Instant lastSeenAt, boolean preexisting) {
        // Get connection
        try (Connection connection = databaseManager.getConnection()) {
            // Don't commit changes until all queries succeed
            connection.setAutoCommit(false);

            try {
                long userID = createUser(connection, preexisting);

                MinecraftAccount minecraftAccount = new MinecraftAccount(userID, uuid, username, lastSeenAt);
                createMinecraftAccount(connection, minecraftAccount);

                connection.commit();
            }
            catch (SQLException e) {
                // Rollback if failure
                connection.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create new user from Minecraft account", e);
        }
    }

    public void createUserFromDiscordAccount(long discordUserID, String username, Instant lastSeenAt, boolean preexisting) {
        // Get connection
        try (Connection connection = databaseManager.getConnection()) {
            // Don't commit changes until all queries succeed
            connection.setAutoCommit(false);

            try {
                long userID = createUser(connection, preexisting);

                DiscordAccount discordAccount = new DiscordAccount(userID, discordUserID, username, lastSeenAt);
                createDiscordAccount(connection, discordAccount);

                connection.commit();
            }
            catch (SQLException e) {
                // Rollback if failure
                connection.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create new user from Discord account", e);
        }
    }

    private long createUser(Connection connection, boolean preexisting) throws SQLException {
        String query = """
                INSERT INTO users (preexisting) VALUES (?)
                """;

        // Create user statement
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Set value in query string
            statement.setBoolean(1, preexisting);

            // Execute
            statement.executeUpdate();

            // Try to get the new user ID
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                else {
                    throw new SQLException("Failed to retrieve generated user ID");
                }
            }
        }
    }

    private void createMinecraftAccount(Connection connection, MinecraftAccount minecraftAccount) throws SQLException {
        String query = """
                INSERT INTO minecraft_accounts (user_id, minecraft_uuid, minecraft_username, last_seen_at)
                VALUES (?, ?, ?, ?)
                """;

        // Create account statement
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set values in query string
            statement.setLong(1, minecraftAccount.getUserID());
            statement.setString(2, minecraftAccount.getUUID().toString());
            statement.setString(3, minecraftAccount.getUsername());

            Instant lastSeenAt = minecraftAccount.getLastSeenAt();
            if (lastSeenAt != null) {
                statement.setTimestamp(4, Timestamp.from(lastSeenAt), utcCalendar);
            }
            else {
                statement.setNull(4, java.sql.Types.TIMESTAMP);
            }


            // Execute
            statement.executeUpdate();
        }
    }

    private void createDiscordAccount(Connection connection, DiscordAccount discordAccount) throws SQLException {
        String query = """
                INSERT INTO discord_accounts (user_id, discord_user_id, discord_username,  last_seen_at)
                VALUES (?, ?, ?, ?)
                """;

        // Create account statement
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set values in query string
            statement.setLong(1, discordAccount.getUserID());
            statement.setLong(2, discordAccount.getDiscordUserID());
            statement.setString(3, discordAccount.getDiscordUsername());
            statement.setTimestamp(4, Timestamp.from(discordAccount.getLastSeenAt()), utcCalendar);

            // Execute
            statement.executeUpdate();
        }
    }

    public void updateLastSeenOnMinecraft(UUID uuid, Instant lastSeenAt) {
        String query = """
                UPDATE minecraft_accounts
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
            throw new RuntimeException("Failed to update last seen timestamp for Minecraft account " + uuid, e);
        }
    }

    public void updateLastSeenOnDiscord(long discordUserID, Instant lastSeenAt) {
        String query = """
                UPDATE discord_accounts
                SET last_seen_at = ?
                WHERE discord_user_id = ?
                AND (last_seen_at IS NULL OR last_seen_at < ?)
                """;

        // Get connection and create query
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // Set timestamp and Discord user ID in query string
            statement.setTimestamp(1, Timestamp.from(lastSeenAt), utcCalendar);
            statement.setLong(2, discordUserID);
            statement.setTimestamp(3, Timestamp.from(lastSeenAt), utcCalendar);

            // Execute
            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to update last seen timestamp for Discord account " + discordUserID, e);
        }
    }
}
