package net.mattlabs.mauvelist.api;

import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.api.config.Config;
import net.mattlabs.mauvelist.api.config.ConfigurateManager;
import net.mattlabs.mauvelist.api.logging.ConsoleOutputHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class MauveListAPI {

    private Logger logger;
    private Config config;
    private final Path dataFolder;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private DatabaseManager databaseManager;

    public MauveListAPI() {
        // Logging with custom handler
        this.logger = Logger.getLogger(MauveListAPI.class.getName());
        logger.setUseParentHandlers(false);
        logger.addHandler(new ConsoleOutputHandler());

        this.dataFolder = Path.of("MauveListAPI");
    }

    static void main(String[] args) {
        // Create and start API
        MauveListAPI api = new MauveListAPI();
        // Stop hook
        Runtime.getRuntime().addShutdownHook(new Thread(api::stop));

        api.start();
        api.awaitShutdown();
    }

    public void start() {
        // Configurate Section

        config = null;

        // Data Folder
        try {
            Files.createDirectories(dataFolder);
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot create directory: " + dataFolder);
        }
        // Initialize Configurate Manager
        ConfigurateManager configurateManager = new ConfigurateManager(dataFolder.toFile(), logger);
        // Add config
        configurateManager.add("config.conf", TypeToken.get(Config.class), new Config(), Config::new);
        // Try to save default values
        if (!configurateManager.saveDefaults("config.conf")) throw new IllegalStateException("Failed to save default config");
        // Load config
        configurateManager.load("config.conf");
        // Save/update config
        configurateManager.save("config.conf");

        config = configurateManager.get("config.conf");

        // MariaDB Section
        logger.info("Connecting to database...");

        // Initialize Manager
        Config.Database databaseConfig = config.getDatabase();
        try {
            databaseManager = new DatabaseManager(
                    databaseConfig.getHostname(),
                    databaseConfig.getPort(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword()
            );
            logger.info("Connection to database successful!");
        } catch (SQLException e) {
            logger.severe("Initializing database manager failed with message: " + e.getMessage());
        }

        // Test Query
        logger.info("Running test query...");
        try {
            Connection connection = databaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 'Hello World!'");
            resultSet.first();
            logger.info("Database query result: " + resultSet.getString(1));
        }
        catch (SQLException e) {
            logger.severe("SQL error: " + e.getMessage());
        }

        logger.info("MauveList API Started!");
    }

    public void awaitShutdown() {
        try {
            shutdownLatch.await();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        // Close database connections
        databaseManager.close();

        shutdownLatch.countDown();

        logger.info("MauveList API Stopped!");
    }

    public Logger getLogger() {
        return logger;
    }
}
