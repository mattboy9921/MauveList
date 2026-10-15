package net.mattlabs.mauvelist.plugin.listeners;

import net.mattlabs.mauvelist.common.communication.ClientCommunicationManager;
import net.mattlabs.mauvelist.common.communication.Endpoints;
import net.mattlabs.mauvelist.common.records.ApiRequest;
import net.mattlabs.mauvelist.common.records.MinecraftActivityRequest;
import net.mattlabs.mauvelist.plugin.MauveList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;

public class JoinListener implements Listener {

    private final ClientCommunicationManager clientCommunicationManager;

    public JoinListener() {
        clientCommunicationManager = MauveList.getInstance().getCommunicationManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        MinecraftActivityRequest minecraftActivityRequest = new MinecraftActivityRequest(player.getName(), Instant.now());
        ApiRequest<MinecraftActivityRequest> request = new ApiRequest<>(Endpoints.minecraftActivity(player.getUniqueId()), minecraftActivityRequest);

        clientCommunicationManager.sendPostRequest(request);
    }
}
