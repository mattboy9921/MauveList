package net.mattlabs.mauvelist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.PlayerManager;
import net.mattlabs.mauvelist.util.PlayerUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mauvelist|ml")
@CommandPermission("mauvelist.admin")
public class MauveListCommand extends BaseCommand {

    MauveList mauveList = MauveList.getInstance();
    PlayerManager playerManager = mauveList.getPlayerManager();
    private Permission permission = MauveList.getPermission();

    @Default
    @Description("MauveList base command.")
    public void onDefault(CommandSender commandSender) {
        mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().version());
    }

    @Subcommand("reload")
    @Description("Reloads configuration.")
    public void onReload(CommandSender commandSender) {
        mauveList.reload();
        if (commandSender instanceof Player) mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().reloaded());
    }

    @Subcommand("list")
    @Description("Shows last 10 joined nonmembers.")
    public void onList(CommandSender commandSender) {
        mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().lastTenGuestsHeading());
        for (int i = 0; i < playerManager.getNonMemberName().size(); i++)
            mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().lastTenGuestsListing(
                    playerManager.getNonMemberName().get(i),
                    playerManager.getNonMemberUUID().get(i).toString(),
                    playerManager.getNonMemberDate().get(i)
            ));
    }

    @Subcommand("add")
    @Description("Adds specified player to member list.")
    public void onAdd(CommandSender commandSender, String name, String discordID) {
        try {
            PlayerUtils.addPlayer(name, discordID);
        }
        catch (NullPointerException | NumberFormatException e) {
            mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().couldNotAdd());
            mauveList.getLogger().warning("Adding " + name + " has failed with error: " + e.getMessage());
            return;
        }

        mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().nowAMember(name));
        mauveList.getLogger().info(name + " is now a member!");
    }

    @Subcommand("applymessage|am")
    @Description("Sends the application message to the relavent channel.")
    public void onApplyButton() {
        if (mauveList.getConfigML().isEnableDiscord()) {
            MessageBuilder builder = new MessageBuilder();
            builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                    .setDescription(mauveList.getConfigML().getApplyBody())
                    .setColor(2664261)
                    .build());
            builder.setActionRows(ActionRow.of(Button.success("apply", "Apply")));
            mauveList.getJda().getTextChannelById(mauveList.getConfigML().getApplyChannel()).sendMessage(builder.build()).queue();
            mauveList.getLogger().info("Apply message has been sent.");
        }
        else
            mauveList.getServer().getLogger().warning("Discord support is disabled!");
    }
}
