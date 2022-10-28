package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private PlayerManager playerManager = MauveList.getInstance().getPlayerManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // Player is a guest, set spectator and tp to spawn
        if (player.hasPermission("mauvelist.grey")) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

            Bukkit.getScheduler().runTaskLater(MauveList.getInstance(),
                    () -> MauveList.getInstance().getLogger().info(player.getName() + " has logged in as a guest."),
                    20);

            if (!playerManager.playerExists(player)) playerManager.addPlayer(player);
        }
        // Player is a member
        else {
            // First join, set to survival, tp to spawn
            if (playerManager.isNew(player)) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

                playerManager.setNew(player, false);

                Bukkit.getScheduler().runTaskLater(MauveList.getInstance(),
                        () -> MauveList.getInstance().getLogger().info(player.getName() + " has logged in as a member for the first time."),
                        20);
            }
        }
    }
}
