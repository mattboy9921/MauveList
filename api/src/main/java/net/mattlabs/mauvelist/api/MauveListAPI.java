package net.mattlabs.mauvelist.api;

import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.api.config.Config;
import net.mattlabs.mauvelist.api.config.ConfigurateManager;
import net.mattlabs.mauvelist.api.logging.ConsoleOutputHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class MauveListAPI {

    private Logger logger;
    private Config config;
    private final Path dataFolder;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

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
        shutdownLatch.countDown();

        logger.info("MauveList API Stopped!");
    }

    public Logger getLogger() {
        return logger;
    }
}
