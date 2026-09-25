package net.mattlabs.mauvelist.plugin;

import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.common.ConfigurateManager;
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

        initializeConfig();

        String hostname = config.getConnection().getHostname();
        int port = config.getConnection().getPort();
        String baseURL = "http://" + hostname + ":" + port;

        communicationManager = new CommunicationManager(baseURL);

        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);

        logger.info("Mauvelist plugin started!");
    }

    private void initializeConfig() {
        // Initialize Configurate Manager
        configurateManager = new ConfigurateManager(getDataFolder(), getLogger());

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

    public static MauveList getInstance() {
        return instance;
    }

    public CommunicationManager getCommunicationManager() {
        return communicationManager;
    }
}
