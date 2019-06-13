package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public final class QuitListener implements Listener {

    private File playerData = null, essentialsPlayerData = null;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("ml.grey")) {
            if (playerData == null) {
                String worldName = MauveList.INSTANCE.getWorldName();
                World world = Bukkit.getServer().getWorld(worldName);

                playerData = new File(world.getWorldFolder(), "playerdata");
            }
            Bukkit.getScheduler().runTask(MauveList.INSTANCE, () -> {
                File playerFile = new File(playerData, player.getUniqueId() + ".dat");
                playerFile.delete();
            });

            if (MauveList.INSTANCE.isEssentialsAvailable()) {
                if (essentialsPlayerData == null) {
                    essentialsPlayerData = new File(MauveList.INSTANCE.getDataFolder().getParentFile(), "Essentials/userdata");
                }
                Bukkit.getScheduler().runTaskLater(MauveList.INSTANCE, () -> {
                    File playerFile = new File(essentialsPlayerData, player.getUniqueId() + ".yml");
                    playerFile.delete();
                }, 10);
            }
        }
    }
}