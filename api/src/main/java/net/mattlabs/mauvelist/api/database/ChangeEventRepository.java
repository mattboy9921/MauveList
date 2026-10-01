package net.mattlabs.mauvelist.api.database;

import net.mattlabs.mauvelist.api.MauveListAPI;
import net.mattlabs.mauvelist.common.records.ChangeEventRecord;
import net.mattlabs.mauvelist.common.records.EventType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChangeEventRepository {

    private final DatabaseManager databaseManager;

    public ChangeEventRepository() {
        databaseManager = MauveListAPI.getInstance().getDatabaseManager();
    }

    public List<ChangeEventRecord> getChangeEventsAfter(long eventId) {
        String query = """
                SELECT *
                FROM change_events
                WHERE id > ?
                ORDER BY id ASC
                """;

        // Get connection and create query
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            // Set event ID in query string
            statement.setLong(1, eventId);

            // Execute
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ChangeEventRecord> changeEvents = new ArrayList<>();

                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    long userID = resultSet.getLong("user_id");
                    Long membershipID = resultSet.getLong("membership_id");
                    Long banID = resultSet.getLong("ban_id");
                    Long applicationID = resultSet.getLong("application_id");
                    EventType eventType = resultSet.getObject("event_type", EventType.class);

                    ChangeEventRecord changeEventRecord = new ChangeEventRecord(id, userID, membershipID, banID, applicationID, eventType);

                    changeEvents.add(changeEventRecord);
                }

                return changeEvents;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to get change events after id: " + eventId, e);
        }
    }
}
