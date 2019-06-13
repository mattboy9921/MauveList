package net.mattlabs.mauvelist;

import co.aikar.commands.PaperCommandManager;
import net.mattlabs.mauvelist.listeners.JoinListener;
import net.mattlabs.mauvelist.listeners.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    private Queue<UUID> nonMomberList = new LinkedList<>();
    public static MauveList INSTANCE = null;

    public void onEnable() {

        INSTANCE = this;
        essentials = new File(getDataFolder().getParentFile(), "Essentials").isDirectory();
        List<World> worlds = Bukkit.getWorlds();
        world = worlds.get(0).getName();

        // Register listeners
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        // Register commands with ACF


        // EssentialsX support
        if (isEssentialsAvailable()) getLogger().log(Level.INFO, "Essentials found - Enabling support...");

        getLogger().log(Level.INFO, "MauveList loaded - By mattboy9921 (Special thanks to RoyCurtis)");
    }

    public void onDisable() {

    }

    public Queue getNonMemberList() {
        return nonMomberList;
    }

    public boolean isEssentialsAvailable() {
        return essentials;
    }

    public String getWorldName() {
        return world;
    }
}
