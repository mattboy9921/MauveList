package net.mattlabs.mauvelist.api;

import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.api.communication.CommunicationManager;
import net.mattlabs.mauvelist.api.config.Config;
import net.mattlabs.mauvelist.api.logging.ConsoleOutputHandler;
import net.mattlabs.mauvelist.common.ConfigurateManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MauveListAPI {

    private Logger logger;
    private Config config;
    private final Path dataFolder;
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private static MauveListAPI instance;

    private DatabaseManager databaseManager;
    private CommunicationManager communicationManager;

    public MauveListAPI() {
        initializeLogging();

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

        initializeConfig();
        initializeDatabase();
        initializeCommunication();

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

    private void initializeLogging() {
        // Logging with custom handler
        Logger rootLogger = Logger.getLogger("");

        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        rootLogger.addHandler(new ConsoleOutputHandler());
        rootLogger.setLevel(Level.INFO);

        this.logger = Logger.getLogger(MauveListAPI.class.getName());
    }

    private void initializeConfig() {
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
}
