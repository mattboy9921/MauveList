package net.mattlabs.mauvelist;

import io.leangen.geantyref.TypeToken;
import net.mattlabs.mauvelist.config.Config;
import net.mattlabs.mauvelist.config.ConfigurateManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MauveList extends JavaPlugin {

    private Logger logger;
    private ConfigurateManager configurateManager;
    private Config config;

    public void onEnable() {
        logger = getLogger();
        logger.info("Starting Mauvelist plugin...");

        initializeConfig();

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
}
