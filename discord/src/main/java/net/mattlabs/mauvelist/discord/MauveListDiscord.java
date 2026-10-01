package net.mattlabs.mauvelist.discord;

import net.mattlabs.mauvelist.common.communication.CommunicationManager;
import net.mattlabs.mauvelist.common.config.ConfigTools;
import net.mattlabs.mauvelist.common.config.ConfigurateManager;
import net.mattlabs.mauvelist.common.logging.LoggingTools;
import net.mattlabs.mauvelist.discord.config.Config;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MauveListDiscord {

    private final Logger logger;
    private Config config;
    private final Path dataFolder;
    private ConfigurateManager configurateManager;
    private CommunicationManager communicationManager;
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    public MauveListDiscord() {
        logger = LoggingTools.initializeLogging(MauveListDiscord.class.getName());

        this.dataFolder = Path.of("MauveListDiscord");
    }

    static void main(String[] args) {
        // Create and start Discord bot
        MauveListDiscord bot = new  MauveListDiscord();
        // Stop hook
        Runtime.getRuntime().addShutdownHook(new Thread(bot::stop));

        bot.start();
    }

    public void start() {
        logger.info("Starting MauveList Discord bot...");

        configurateManager = new ConfigurateManager(dataFolder.toFile(), logger);
        config = ConfigTools.initializeConfig(
                dataFolder,
                configurateManager,
                "config.conf",
                Config.class,
                Config::new);

        String hostname = config.getConnection().getHostname();
        int port = config.getConnection().getPort();
        String baseURL = "http://" + hostname + ":" + port;

        communicationManager = new CommunicationManager(baseURL, logger, configurateManager);

        logger.info("MauveList Discord bot started!");
    }

    public void stop() {
        if (stopping.compareAndSet(false, true)) {
            logger.info("Stopping MauveList Discord bot...");

            communicationManager.shutdown();

            logger.info("MauveList Discord bot stopped!");
        }
    }
}
