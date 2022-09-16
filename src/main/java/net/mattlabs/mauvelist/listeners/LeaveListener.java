package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class LeaveListener implements Listener {

    private File playerData = null, essentialsPlayerData = null;

    // Player quits
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReason().equals("Rejoin in 30 seconds, you are now a member!"))
            deletePlayerData(event.getPlayer());
    }

    // Player kicked for permissions refresh
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        deletePlayerData(event.getPlayer());
    }

    // Delete the player's data file and Essentails data file if enabled
    private void deletePlayerData(Player player) {
        if (MauveList.getInstance().getConfigML().isDeletePlayerData()) {
            if (player.hasPermission("mauvelist.grey")) {
                // Player data file
                if (playerData == null) {
                    String worldName = MauveList.getInstance().getWorldName();
                    World world = Bukkit.getServer().getWorld(worldName);

                    playerData = new File(world.getWorldFolder(), "playerdata");
                }
                Bukkit.getScheduler().runTaskAsynchronously(MauveList.getInstance(), () -> {
                    File playerFile = new File(playerData, player.getUniqueId() + ".dat");
                    playerFile.delete();
                });

                // Essentials
                if (MauveList.getInstance().isEssentialsAvailable()) {
                    if (essentialsPlayerData == null) {
                        essentialsPlayerData = new File(MauveList.getInstance().getDataFolder().getParentFile(), "Essentials/userdata");
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(MauveList.getInstance(), () -> {
                        File playerFile = new File(essentialsPlayerData, player.getUniqueId() + ".yml");
                        playerFile.delete();
                    });
                }
            }
        }
    }
}
