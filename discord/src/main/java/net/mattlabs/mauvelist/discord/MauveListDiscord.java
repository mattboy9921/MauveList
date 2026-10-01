package net.mattlabs.mauvelist.discord;

import net.mattlabs.mauvelist.common.logging.Logging;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MauveListDiscord {

    private final Logger logger;
    private final AtomicBoolean stopping = new AtomicBoolean(false);

    public MauveListDiscord() {
        logger = Logging.initializeLogging(MauveListDiscord.class.getName());
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

        logger.info("MauveList Discord bot started!");
    }

    public void stop() {
        if (stopping.compareAndSet(false, true)) {
            logger.info("Stopping MauveList Discord bot...");

            logger.info("MauveList Discord bot stopped!");
        }
    }
}
