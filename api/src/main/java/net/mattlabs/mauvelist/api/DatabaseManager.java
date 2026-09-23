package net.mattlabs.mauvelist.api;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final MariaDbPoolDataSource dataSource;

    public DatabaseManager(String hostname, int port, String database, String username, String password) throws SQLException {
        dataSource = new MariaDbPoolDataSource();
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }

    public void migrate() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        flyway.migrate();
    }
}
