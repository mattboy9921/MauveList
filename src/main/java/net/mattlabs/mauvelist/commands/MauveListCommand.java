package net.mattlabs.mauvelist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.messaging.Messages;
import net.mattlabs.mauvelist.util.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@CommandAlias("mauvelist|ml")
@CommandPermission("mauvelist.admin")
public class MauveListCommand extends BaseCommand {

    PlayerManager playerManager = MauveList.getInstance().getPlayerManager();

    @Default
    @Description("MauveList base command.")
    public void onDefault(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) MauveList.getInstance().getLogger().info("Version " +
                Bukkit.getPluginManager().getPlugin("MauveList").getDescription().getVersion());
        else commandSender.spigot().sendMessage(Messages.version());
    }
}
