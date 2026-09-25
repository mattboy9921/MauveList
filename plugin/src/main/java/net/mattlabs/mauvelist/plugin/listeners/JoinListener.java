package net.mattlabs.mauvelist.plugin.listeners;

import net.mattlabs.mauvelist.plugin.CommunicationManager;
import net.mattlabs.mauvelist.plugin.MauveList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private CommunicationManager communicationManager;

    public JoinListener() {
        communicationManager = MauveList.getInstance().getCommunicationManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        communicationManager.playerActivity(player.getUniqueId(), player.getName());
    }
}
