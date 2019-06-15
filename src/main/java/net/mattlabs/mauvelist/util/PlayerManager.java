package net.mattlabs.mauvelist.util;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

public class PlayerManager implements Runnable{

    private ConfigManager configManager;
    private LinkedList<UUID> nonMemberUUID;
    private LinkedList<String> nonMemberName, nonMemberDate;

    public PlayerManager() {
        configManager = MauveList.getInstance().getConfigManager();
    }

    public void addPlayer(Player player) {
        nonMemberName.remove(player.getName());
        nonMemberName.addFirst(player.getName());
        if (nonMemberName.size() == 11) nonMemberName.removeLast();
        nonMemberUUID.remove(player.getUniqueId());
        nonMemberUUID.addFirst(player.getUniqueId());
        if (nonMemberUUID.size() == 11) nonMemberUUID.removeLast();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(MauveList.getInstance(), this);
    }

    public void loadPlayerData() {
        nonMemberUUID = new LinkedList<>();
        nonMemberName = new LinkedList<>();
        nonMemberDate = new LinkedList<>();
        ArrayList<String> loadList = (ArrayList<String>) configManager.getFileConfig("data.yml").getStringList("nonMemberList");
        for (String string : loadList) {
            String[] parts = string.split("\\|");
            nonMemberUUID.add(UUID.fromString(parts[0]));
            nonMemberName.add(parts[1]);
            Date date = new Date(Bukkit.getOfflinePlayer(UUID.fromString(parts[0])).getLastLogin());
            DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            nonMemberDate.add(dateFormat.format(date));
        }
    }

    public LinkedList<UUID> getNonMemberUUID() {
        return nonMemberUUID;
    }

    public LinkedList<String> getNonMemberName() {
        return nonMemberName;
    }

    public LinkedList<String> getNonMemberDate() {
        return nonMemberDate;
    }

    // Saves data to file asynchronously
    @Override
    public void run() {
        ArrayList<String> saveList = new ArrayList<>();
        for (int i = 0; i < nonMemberName.size(); i++) saveList.add(nonMemberUUID.get(i) + "|" + nonMemberName.get(i));

        configManager.getFileConfig("data.yml").set("nonMemberList", saveList);
        configManager.saveConfig("data.yml");
    }
}
