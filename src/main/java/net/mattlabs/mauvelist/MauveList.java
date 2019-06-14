package net.mattlabs.mauvelist;

import co.aikar.commands.PaperCommandManager;
import net.mattlabs.mauvelist.commands.MauveListCommand;
import net.mattlabs.mauvelist.listeners.JoinListener;
import net.mattlabs.mauvelist.listeners.QuitListener;
import net.mattlabs.mauvelist.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    private LinkedList<UUID> nonMomberList = new LinkedList<>();
    private static MauveList instance;
    public PaperCommandManager paperCommandManager;
    private ConfigManager configManager;

    public void onEnable() {

        instance = this;
        essentials = new File(getDataFolder().getParentFile(), "Essentials").isDirectory();
        List<World> worlds = Bukkit.getWorlds();
        world = worlds.get(0).getName();

        // Configuration Section
        configManager = new ConfigManager(this);
        configManager.loadConfigFiles(
                new ConfigManager.ConfigPath(
                        "data.yml",
                        "data.yml",
                        "data.yml"));
        configManager.saveAllConfigs(false);

        // Load Player Data
        ArrayList<String> loadList = (ArrayList<String>) configManager.getFileConfig("data.yml").getStringList("nonMemberList");
        for (String string : loadList) nonMomberList.add(UUID.fromString(string));
        this.getLogger().info(nonMomberList.toString());

        // Register ACF
        paperCommandManager = new PaperCommandManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        // Register commands with ACF
        paperCommandManager.registerCommand(new MauveListCommand());

        // EssentialsX support
        if (isEssentialsAvailable()) getLogger().log(Level.INFO, "Essentials found - Enabling support...");

        getLogger().log(Level.INFO, "MauveList loaded - By mattboy9921 (Special thanks to RoyCurtis)");
    }

    public void onDisable() {

    }

    public static MauveList getInstance() {
        return instance;
    }

    public LinkedList<UUID> getNonMemberList() {
        return nonMomberList;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public boolean isEssentialsAvailable() {
        return essentials;
    }

    public String getWorldName() {
        return world;
    }
}
