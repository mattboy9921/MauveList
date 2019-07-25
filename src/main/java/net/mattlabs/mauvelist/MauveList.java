package net.mattlabs.mauvelist;

import co.aikar.commands.PaperCommandManager;
import net.mattlabs.configmanager.ConfigManager;
import net.mattlabs.mauvelist.commands.MauveListCommand;
import net.mattlabs.mauvelist.listeners.JoinListener;
import net.mattlabs.mauvelist.listeners.KickListener;
import net.mattlabs.mauvelist.listeners.QuitListener;
import net.mattlabs.mauvelist.util.PlayerManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    private static MauveList instance;
    public PaperCommandManager paperCommandManager;
    private ConfigManager configManager;
    private PlayerManager playerManager;
    private static Permission permission = null;

    public void onEnable() {

        instance = this;
        essentials = new File(getDataFolder().getParentFile(), "Essentials").isDirectory();
        List<World> worlds = Bukkit.getWorlds();
        world = worlds.get(0).getName();

        // Vault Check
        if (!hasVault()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Vault Setup
        if (!setupPermissions()) {
            this.getLogger().severe("Disabled due to Vault Permissions error!");
            this.getLogger().severe("Is there a permission plugin installed?");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configuration Section
        configManager = new ConfigManager(this);
        configManager.loadConfigFiles(
                new ConfigManager.ConfigPath(
                        "config.yml",
                        "config.yml",
                        "config.yml"),
                new ConfigManager.ConfigPath(
                        "data.yml",
                        "data.yml",
                        "data.yml"));
        configManager.saveAllConfigs(false);

        // Load Player Manager
        playerManager = new PlayerManager();
        playerManager.loadPlayerData();

        // Register ACF
        paperCommandManager = new PaperCommandManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new KickListener(), this);

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

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static Permission getPermission() {
        return permission;
    }

    public boolean isEssentialsAvailable() {
        return essentials;
    }

    public String getWorldName() {
        return world;
    }

    // Vault Helper Methods

    private boolean hasVault() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            permission = rsp.getProvider();
            return permission != null;
        }
        else return false;
    }
}
