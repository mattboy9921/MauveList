package net.mattlabs.mauvelist.api;

import net.mattlabs.mauvelist.api.communication.CommunicationManager;
import net.mattlabs.mauvelist.api.config.Config;
import net.mattlabs.mauvelist.api.database.DatabaseManager;
import net.mattlabs.mauvelist.common.config.ConfigTools;
import net.mattlabs.mauvelist.common.config.ConfigurateManager;
import net.mattlabs.mauvelist.common.logging.LoggingTools;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MauveListAPI {

    private final Logger logger;
    private Config config;
    private final Path dataFolder;
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private static MauveListAPI instance;

    private DatabaseManager databaseManager;
    private CommunicationManager communicationManager;

    public MauveListAPI() {
        logger = LoggingTools.initializeLogging(MauveListAPI.class.getName());

        this.dataFolder = Path.of("MauveListAPI");

        instance = this;
    }

    static void main(String[] args) {
        // Create and start API
        MauveListAPI api = new MauveListAPI();
        // Stop hook
        Runtime.getRuntime().addShutdownHook(new Thread(api::stop));

        api.start();
    }

    public void start() {
        logger.info("Starting MauveListAPI...");

        try {
            config = ConfigTools.initializeConfig(
                    dataFolder,
                    new ConfigurateManager(dataFolder.toFile(), logger),
                    "config.conf",
                    Config.class,
                    Config::new);

            initializeDatabase();
            initializeCommunication();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "MauveList API startup failed!", e);
            stop();
            return;
        }

        logger.info("MauveList API started!");
    }

    public void stop() {
        if (stopping.compareAndSet(false, true)) {
            logger.info("Stopping MauveListAPI...");

            // Close database connections
            if (databaseManager != null) {
                logger.info("Closing database connections...");

                databaseManager.close();

                logger.info("Database disconnected!");
            }

            // Stop communication
            if (communicationManager != null) {
                logger.info("Stopping communication...");

                communicationManager.stop();

                logger.info("Communication stopped!");
            }

            logger.info("MauveList API Stopped!");
        }
    }

    private void initializeDatabase() {
        logger.info("Connecting to database...");

        // Initialize Manager
        Config.Database databaseConfig = config.getDatabase();
        try {
            databaseManager = new DatabaseManager(
                    databaseConfig.getHostname(),
                    databaseConfig.getPort(),
                    databaseConfig.getDatabase(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword()
            );
            logger.info("Connection to database successful!");
        }
        catch (SQLException e) {
            throw new IllegalStateException("Initializing database manager failed", e);
        }

        // Migrations
        logger.info("Checking schema version...");
        if (databaseManager.migrate()) {
            logger.info("Database up to date!");
        }
        else {
            throw new IllegalStateException("Database migration failure");
        }
    }

    private void initializeCommunication() {
        logger.info("Initializing communication...");

        communicationManager = new CommunicationManager();
        communicationManager.start();

        logger.info("Communication initialization successful!");
    }

    public static MauveListAPI getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
