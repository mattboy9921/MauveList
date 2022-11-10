package net.mattlabs.mauvelist.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.PlayerUtils;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
@CommandAlias("mauvelist|ml")
@CommandPermission("mauvelist.admin")
public class MauveListCommand extends BaseCommand {

    private final MauveList mauveList = MauveList.getInstance();

    @Default
    @Description("MauveList base command.")
    public void onDefault(CommandSender commandSender) {
        mauveList.getPlatform().sender(commandSender).sendMessage(mauveList.getMessages().version());
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

            TextChannel applyChannel = mauveList.getJda().getTextChannelById(mauveList.getConfigML().getApplyChannel());
            if (applyChannel != null) {
                applyChannel.sendMessage(builder.build()).queue();
                mauveList.getLogger().info("Apply message has been sent.");
            }
            else mauveList.getLogger().info("Apply message could not be sent, please check apply channel value in config!");
        }
        else
            mauveList.getServer().getLogger().warning("Discord support is disabled!");
    }
}
