package net.mattlabs.mauvelist.plugin.listeners;

import net.mattlabs.mauvelist.common.communication.CommunicationManager;
import net.mattlabs.mauvelist.common.communication.Endpoints;
import net.mattlabs.mauvelist.common.records.ApiRequest;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;
import net.mattlabs.mauvelist.plugin.MauveList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;

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

        PlayerActivityRequest playerActivityRequest = new PlayerActivityRequest(player.getName(), Instant.now());
        ApiRequest<PlayerActivityRequest> request = new ApiRequest<>(Endpoints.playerActivity(player.getUniqueId()), playerActivityRequest);

        communicationManager.sendPostRequest(request);
    }
}
