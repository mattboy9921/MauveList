package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class JoinListener implements Listener {

    private Queue<UUID> nonMemberList = MauveList.INSTANCE.getNonMemberList();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("ml.grey")) {
            player.setGameMode(GameMode.SPECTATOR);

            nonMemberList.add(player.getUniqueId());
            if (nonMemberList.size() == 11) nonMemberList.remove();
        }


    }
}
