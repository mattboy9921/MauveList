package net.mattlabs.mauvelist;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    public static MauveList INSTANCE = null;

    public void onEnable() {

        INSTANCE = this;
        essentials = new File(getDataFolder().getParentFile(), "Essentials").isDirectory();
        List<World> worlds = Bukkit.getWorlds();
        world = worlds.get(0).getName();

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        if (isEssentialsAvailable()) getLogger().log(Level.INFO, "Essentials found - Enabling support...");
        getLogger().log(Level.INFO, "MauveList loaded - By mattboy9921 (Special thanks to RoyCurtis)");
    }

    public void onDisable() {

    }

    public boolean isEssentialsAvailable() {
        return essentials;
    }

    public String getWorldName() {
        return world;
    }
}
