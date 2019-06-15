package net.mattlabs.mauvelist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
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
    Permission permission = MauveList.getPermission();

    @Default
    @Description("MauveList base command.")
    public void onDefault(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) MauveList.getInstance().getLogger().info("Version " +
                Bukkit.getPluginManager().getPlugin("MauveList").getDescription().getVersion());
        else commandSender.spigot().sendMessage(Messages.version());
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
        permission.playerAddGroup(null, Bukkit.getOfflinePlayer(name), "noob");
        if (!(commandSender instanceof Player)) MauveList.getInstance().getLogger().info(name + " is now a member!");
        else commandSender.spigot().sendMessage(Messages.nowAMember(name));
    }
}
