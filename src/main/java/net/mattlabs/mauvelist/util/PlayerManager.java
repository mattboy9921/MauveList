package net.mattlabs.mauvelist.util;

import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

/* PlayerManager keeps track of every player that joins the server */
public class PlayerManager {

    private final MauveList mauveList = MauveList.getInstance();
    private final Map<UUID, MauvePlayerData> mauvePlayerMap = mauveList.getData().getMauvePlayerMap();

    public void addPlayer(OfflinePlayer player) {
        mauvePlayerMap.put(player.getUniqueId(), new MauvePlayerData());
        save();
    }

    public boolean playerExists(OfflinePlayer player) {
        return mauvePlayerMap.containsKey(player.getUniqueId());
    }

    public boolean isNew(OfflinePlayer player) {
        return mauvePlayerMap.get(player.getUniqueId()).isNew();
    }
    public void setNew(OfflinePlayer player, boolean isNew) {
        mauvePlayerMap.get(player.getUniqueId()).setNew(isNew);
        save();
    }

    private void save() {
        Bukkit.getScheduler().runTaskAsynchronously(mauveList, () -> mauveList.getConfigurateManager().save("data.conf"));
    }
}
