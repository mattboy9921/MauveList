package net.mattlabs.mauvelist.listeners;

import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class JoinListener implements Runnable, Listener {

    private LinkedList<UUID> nonMemberList = MauveList.getInstance().getNonMemberList();
    private ConfigManager configManager = MauveList.getInstance().getConfigManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("ml.grey")) {
            player.setGameMode(GameMode.SPECTATOR);

            nonMemberList.remove(player.getUniqueId());
            nonMemberList.addFirst(player.getUniqueId());
            if (nonMemberList.size() == 11) nonMemberList.removeLast();

            Bukkit.getServer().getScheduler().runTaskAsynchronously(MauveList.getInstance(), this);
        }
    }

    // Saves configuration file on change asynchronously
    @Override
    public void run() {
        ArrayList<String> saveList = new ArrayList<>();
        for (UUID uuid : nonMemberList) saveList.add(uuid.toString());

        configManager.getFileConfig("data.yml").set("nonMemberList", saveList);
        configManager.saveConfig("data.yml");
    }
}
