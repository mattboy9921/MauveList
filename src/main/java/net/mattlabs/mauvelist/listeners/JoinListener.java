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

        if (player.hasPermission("mauvelist.grey")) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

            Bukkit.getScheduler().runTaskLater(MauveList.getInstance(),
                    () -> MauveList.getInstance().getLogger().info(player.getName() + " has logged in as a guest."),
                    20);

            playerManager.addPlayer(player);
        }
    }
}
