package net.mattlabs.mauvelist;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class MauveList extends JavaPlugin {

    private boolean essentials;
    private String world;
    public static MauveList INSTANCE = null;

    public void onEnable() {

        INSTANCE = this;
        essentials = new File(getDataFolder(), "essentials").isDirectory();
        List<World> worlds = Bukkit.getWorlds();
        world = worlds.get(0).getName();

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
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
