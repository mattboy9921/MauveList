package net.mattlabs.mauvelist.util;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.User;
import net.mattlabs.mauvelist.MauveList;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerUtils {

    private static final MauveList mauveList = MauveList.getInstance();
    private static final Permission permission = MauveList.getPermission();

    // Adds a player to the members group
    public static void addPlayer(String name, String discordID) throws NullPointerException, NumberFormatException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);

        // Check for valid Minecraft username
        if (!minecraftUsernameIsValid(name)) throw new NullPointerException("Invalid Minecraft username");

        // Check for valid Discord ID
        User user = mauveList.getJda().retrieveUserById(discordID).complete();
        if (user == null) throw new NullPointerException("Invalid Discord user ID");
        // Link Discord
        Bukkit.getScheduler().runTaskAsynchronously(mauveList, () -> DiscordSRV.getPlugin().getAccountLinkManager().link(user.getId(), Bukkit.getPlayerUniqueId(name)));

        mauveList.getServer().getScheduler().runTaskAsynchronously(mauveList, () -> {
            if (mauveList.getConfigML().getPermissionType().equalsIgnoreCase("set")) {
                String[] groups = permission.getPlayerGroups(null, player);
                for (String group : groups) {
                    if (!(mauveList.getConfigML().getMemberGroup().equals(group)))
                        permission.playerRemoveGroup(null, player, group);
                }
            }
            if (!(permission.playerAddGroup(null, player,
                    mauveList.getConfigML().getMemberGroup()))) {
                mauveList.getLogger().warning("Could not add member, check member-group in config!");
            }
            else {
                mauveList.getLogger().info(name + " is now a member!");
                if (player.isOnline()) MauveList.getInstance().getServer().getScheduler().runTask(MauveList.getInstance(), () -> {
                    Bukkit.getPlayer(name).kickPlayer("Rejoin in 30 seconds, you are now a member!");
                });
            }
        });
    }

    // Checks if username is valid via length, characters and API
    public static boolean minecraftUsernameIsValid(String username) {
        return username != null && username.length() >= 3 && username.length() <= 16 && username.matches("\\w+") && validateMinecraftUsernameWithAPI(username);
    }

    private static boolean validateMinecraftUsernameWithAPI(String username){
        String urlString = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500);
            connection.setReadTimeout(500);

            int statusCode = connection.getResponseCode();

            return statusCode == 200 || statusCode == 429; // Ignore timeout
        } catch (IOException e) {
            return false;
        }
    }
}
