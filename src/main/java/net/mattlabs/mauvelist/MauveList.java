package net.mattlabs.mauvelist;

import co.aikar.commands.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mattlabs.mauvelist.commands.MauveListCommand;
import net.mattlabs.mauvelist.listeners.JDAListener;
import net.mattlabs.mauvelist.listeners.JoinListener;
import net.mattlabs.mauvelist.listeners.LeaveListener;
import net.mattlabs.mauvelist.messaging.Messages;
import net.mattlabs.mauvelist.util.ApplicationManager;
import net.mattlabs.mauvelist.util.ConfigurateManager;
import net.mattlabs.mauvelist.util.PlayerManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    private static MauveList instance;
    public PaperCommandManager paperCommandManager;
    private PlayerManager playerManager;
    private static Permission permission = null;
    private ConfigurateManager configurateManager;
    private Config config;
    private Data data;
    private BukkitAudiences platform;
    private Messages messages;
    private JDA jda;
    private ApplicationManager applicationManager;

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
        configurateManager = new ConfigurateManager();

        configurateManager.add("config.conf", TypeToken.get(Config.class), new Config(), Config::new);
        configurateManager.add("data.conf", TypeToken.get(Data.class), new Data(), Data::new);

        configurateManager.saveDefaults("config.conf");
        configurateManager.saveDefaults("data.conf");

        configurateManager.load("config.conf");
        configurateManager.load("data.conf");

        configurateManager.save("config.conf");
        configurateManager.save("data.conf");

        config = configurateManager.get("config.conf");
        data = configurateManager.get("data.conf");
        
        // Load Player Manager
        playerManager = new PlayerManager();
        playerManager.loadPlayerData();

        // Register Audience (Messages)
        platform = BukkitAudiences.create(this);

        // Create Messages
        messages = new Messages();

        // Set Up JDA
        if (getConfigML().isEnableDiscord()) {
            try {
                jda = JDABuilder.createDefault(config.getBotToken()).setStatus(getOnlineStatusFromString(config.getBotStatus())).build();
            } catch (LoginException e) {
                e.printStackTrace();
                this.setEnabled(false);
            }
        }

        // Create ApplicationManager
        applicationManager = new ApplicationManager();

        // Register JDA Listener
        if (getConfigML().isEnableDiscord()) jda.addEventListener(new JDAListener());

        // Register ACF
        paperCommandManager = new PaperCommandManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);

        // Register commands with ACF
        paperCommandManager.registerCommand(new MauveListCommand());

        // EssentialsX support
        if (isEssentialsAvailable()) getLogger().log(Level.INFO, "Essentials found - Enabling support...");

        getLogger().log(Level.INFO, "MauveList loaded - By mattboy9921 (Special thanks to RoyCurtis)");
    }

    public void onDisable() {

    }

    public void reload() {
        getLogger().info("Reloading MauveList...");
        configurateManager.reload();
        config = configurateManager.get("config.conf");
        getLogger().info("Configuration reloaded.");
    }

    public static MauveList getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ConfigurateManager getConfigurateManager() {
        return configurateManager;
    }

    public Config getConfigML() {
        return config;
    }

    public Data getData() {
        return data;
    }

    public BukkitAudiences getPlatform() {
        return platform;
    }

    public Messages getMessages() {
        return messages;
    }

    public JDA getJda() {
        return jda;
    }

    public ApplicationManager getApplicationManager() {
        return applicationManager;
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

    private OnlineStatus getOnlineStatusFromString(String status) {
        switch(status) {
            case "invisible":
                return OnlineStatus.INVISIBLE;
            case "dnd":
                return OnlineStatus.DO_NOT_DISTURB;
            case "away":
                return OnlineStatus.IDLE;
            default:
                return OnlineStatus.ONLINE;
        }
    }
}
