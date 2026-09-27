package net.mattlabs.mauvelist.plugin.listeners;

import net.mattlabs.mauvelist.plugin.MauveList;
import net.mattlabs.mauvelist.plugin.communication.CommunicationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final CommunicationManager communicationManager;

    public LeaveListener() {
        communicationManager = MauveList.getInstance().getCommunicationManager();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        onLeave(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onLeave(event.getPlayer());
    }

    private void onLeave(Player player) {
        communicationManager.playerActivity(player.getUniqueId(), player.getName());
    }
}
