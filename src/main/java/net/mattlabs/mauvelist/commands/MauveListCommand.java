package net.mattlabs.mauvelist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.mattlabs.configmanager.ConfigManager;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.messaging.Messages;
import net.mattlabs.mauvelist.util.PlayerManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@CommandAlias("mauvelist|ml")
@CommandPermission("mauvelist.admin")
public class MauveListCommand extends BaseCommand {

    PlayerManager playerManager = MauveList.getInstance().getPlayerManager();
    ConfigManager configManager = MauveList.getInstance().getConfigManager();
    private Permission permission = MauveList.getPermission();

    @Default
    @Description("MauveList base command.")
    public void onDefault(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) MauveList.getInstance().getLogger().info("Version " +
                Bukkit.getPluginManager().getPlugin("MauveList").getDescription().getVersion());
        else commandSender.spigot().sendMessage(Messages.version());
    }

    @Subcommand("reload")
    @Description("Reloads configuration.")
    public void onReload(CommandSender commandSender) {
        configManager.reloadConfig("config.yml");
        if (commandSender instanceof Player) commandSender.spigot().sendMessage(Messages.reloaded());
        MauveList.getInstance().getLogger().info("Configuration reloaded.");
    }

    @Subcommand("list")
    @Description("Shows last 10 joined nonmembers.")
    public void onList(CommandSender commandSender) {
        LinkedList<String> nonMemberName = playerManager.getNonMemberName();
        if (!(commandSender instanceof Player)) {
            MauveList.getInstance().getLogger().info("Last 10 nonmembers:");
            for (String name : nonMemberName) MauveList.getInstance().getLogger().info(" - " + name);
        }
        else {
            commandSender.spigot().sendMessage(Messages.lastTenMembersHeading());
            for (int i = 0; i < playerManager.getNonMemberName().size(); i++)
                commandSender.spigot().sendMessage(Messages.lastTenMembersListing(
                        playerManager.getNonMemberName().get(i),
                        playerManager.getNonMemberUUID().get(i).toString(),
                        playerManager.getNonMemberDate().get(i)));
        }
    }

    @Subcommand("add")
    @Description("Adds specified player to member list.")
    public void onAdd(CommandSender commandSender, String name) {
        MauveList.getInstance().getServer().getScheduler().runTaskAsynchronously(MauveList.getInstance(), () -> {
            if (!(permission.playerAddGroup(null, Bukkit.getOfflinePlayer(name),
                    configManager.getFileConfig("config.yml").getString("member-group")))) {
                if (commandSender instanceof Player) commandSender.spigot().sendMessage(Messages.couldNotAdd());
                MauveList.getInstance().getLogger().warning("Could not add member, check member-group in config!");
            }
            else {
                if (commandSender instanceof Player) commandSender.spigot().sendMessage(Messages.nowAMember(name));
                MauveList.getInstance().getLogger().info(name + " is now a member!");
                if (Bukkit.getOfflinePlayer(name).isOnline()) MauveList.getInstance().getServer().getScheduler().runTask(MauveList.getInstance(), () -> {
                    Bukkit.getPlayer(name).kickPlayer("Rejoin in 30 seconds, you are now a member!");
                });
            }
        });
    }
}
