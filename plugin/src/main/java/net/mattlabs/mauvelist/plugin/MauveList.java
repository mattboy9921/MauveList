package net.mattlabs.mauvelist.plugin;

import net.mattlabs.mauvelist.common.communication.CommunicationManager;
import net.mattlabs.mauvelist.common.config.ConfigTools;
import net.mattlabs.mauvelist.common.config.ConfigurateManager;
import net.mattlabs.mauvelist.plugin.config.Config;
import net.mattlabs.mauvelist.plugin.listeners.JoinListener;
import net.mattlabs.mauvelist.plugin.listeners.LeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MauveList extends JavaPlugin {

    private Logger logger;
    private ConfigurateManager configurateManager;
    private Config config;
    private static MauveList instance;
    private CommunicationManager communicationManager;

    public void onEnable() {
        instance = this;

        logger = getLogger();
        logger.info("Starting Mauvelist plugin...");

        configurateManager = new ConfigurateManager(getDataFolder(), getLogger());
        config = ConfigTools.initializeConfig(
                getDataPath(),
                configurateManager,
                "config.conf",
                Config.class,
                Config::new);

        String hostname = config.getConnection().getHostname();
        int port = config.getConnection().getPort();
        String baseURL = "http://" + hostname + ":" + port;

        communicationManager = new CommunicationManager(baseURL, logger, configurateManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);

        logger.info("Mauvelist plugin started!");
    }

    public void onDisable() {
        logger.info("Disabling MauveList plugin...");

        communicationManager.shutdown();

        logger.info("MauveList plugin disabled!");
    }

    public static MauveList getInstance() {
        return instance;
    }

    public CommunicationManager getCommunicationManager() {
        return communicationManager;
    }

    public ConfigurateManager getConfigurateManager() {
        return configurateManager;
    }
}
