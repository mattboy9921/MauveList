package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.io.File;

public final class KickListener implements Listener {

    private File playerData = null, essentialsPlayerData = null;

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("mauvelist.grey") || event.getReason().equals("Rejoin in 30 seconds, you are now a member!")) {
            if (playerData == null) {
                String worldName = MauveList.getInstance().getWorldName();
                World world = Bukkit.getServer().getWorld(worldName);

                playerData = new File(world.getWorldFolder(), "playerdata");
            }
            Bukkit.getScheduler().runTask(MauveList.getInstance(), () -> {
                File playerFile = new File(playerData, player.getUniqueId() + ".dat");
                playerFile.delete();
            });

            if (MauveList.getInstance().isEssentialsAvailable()) {
                if (essentialsPlayerData == null) {
                    essentialsPlayerData = new File(MauveList.getInstance().getDataFolder().getParentFile(), "Essentials/userdata");
                }
                Bukkit.getScheduler().runTaskLater(MauveList.getInstance(), () -> {
                    File playerFile = new File(essentialsPlayerData, player.getUniqueId() + ".yml");
                    playerFile.delete();
                }, 10);
            }
        }
    }
}