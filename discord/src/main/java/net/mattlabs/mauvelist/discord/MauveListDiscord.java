package net.mattlabs.mauvelist.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.mattlabs.mauvelist.common.communication.ClientCommunicationManager;
import net.mattlabs.mauvelist.common.config.ConfigTools;
import net.mattlabs.mauvelist.common.config.ConfigurateManager;
import net.mattlabs.mauvelist.common.logging.LoggingTools;
import net.mattlabs.mauvelist.discord.config.Config;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MauveListDiscord {

    private final Logger logger;
    private Config config;
    private final Path dataFolder;
    private ConfigurateManager configurateManager;
    private ClientCommunicationManager clientCommunicationManager;
    private JDA jda;
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

        try {
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

            clientCommunicationManager = new ClientCommunicationManager(baseURL, logger, configurateManager);

            initializeDiscord();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "MauveList Discord bot startup failed!", e);
            stop();
            return;
        }

        logger.info("MauveList Discord bot started!");
    }

    public void stop() {
        if (stopping.compareAndSet(false, true)) {
            logger.info("Stopping MauveList Discord bot...");

            clientCommunicationManager.shutdown();
            if (jda != null) jda.shutdown();

            logger.info("MauveList Discord bot stopped!");
        }
    }

    private void initializeDiscord() {
        logger.info("Initializing Discord connection...");

        try {
            jda = JDABuilder.createDefault(config.getBotToken(), GatewayIntent.GUILD_MEMBERS)
                    .build()
                    .awaitReady();

            logger.info("Discord connection successful!");
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Discord connection!", e);
        }
    }
}
