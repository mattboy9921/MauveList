package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.ConfigManager;
import net.mattlabs.mauvelist.util.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedList;
import java.util.UUID;

public class JoinListener implements Listener {

    private LinkedList<UUID> nonMemberList = MauveList.getInstance().getPlayerManager().getNonMemberList();
    private ConfigManager configManager = MauveList.getInstance().getConfigManager();
    private PlayerManager playerManager = MauveList.getInstance().getPlayerManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("ml.grey")) {
            player.setGameMode(GameMode.SPECTATOR);

            playerManager.addPlayer(player);
        }
    }
}
