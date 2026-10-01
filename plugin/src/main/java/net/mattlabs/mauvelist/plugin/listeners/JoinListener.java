package net.mattlabs.mauvelist.plugin.listeners;

import net.mattlabs.mauvelist.common.communication.CommunicationManager;
import net.mattlabs.mauvelist.common.communication.Endpoints;
import net.mattlabs.mauvelist.common.records.ApiRequest;
import net.mattlabs.mauvelist.common.records.PlayerActivityRequest;
import net.mattlabs.mauvelist.plugin.MauveList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;

public class JoinListener implements Listener {

    private final CommunicationManager communicationManager;

    public JoinListener() {
        communicationManager = MauveList.getInstance().getCommunicationManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        PlayerActivityRequest playerActivityRequest = new PlayerActivityRequest(player.getName(), Instant.now());
        ApiRequest<PlayerActivityRequest> request = new ApiRequest<>(Endpoints.playerActivity(player.getUniqueId()), playerActivityRequest);

        communicationManager.sendPostRequest(request);
    }
}
