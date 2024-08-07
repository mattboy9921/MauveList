package net.mattlabs.mauvelist.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;

import java.util.ArrayList;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class Messages {

    private final MauveList mauveList = MauveList.getInstance();

    public Component version() {
        return Component.text()
                .append(Component.text("[", GRAY))
                .append(Component.text("MauveList", TextColor.fromHexString("#5B4253")))
                .append(Component.text("] ", GRAY))
                .append(Component.text("Version: " + Bukkit.getPluginManager().getPlugin("MauveList").getDescription().getVersion(), WHITE))
                .build();
    }

    public Component lastTenGuestsHeading() {
        return Component.text()
                .append(Component.text("[", GRAY))
                .append(Component.text("MauveList", TextColor.fromHexString("#5B4253")))
                .append(Component.text("] ", GRAY))
                .append(Component.text("Last 10 guests:", WHITE))
                .build();
    }

    public Component lastTenGuestsListing(String name, String uuid, String lastJoin) {
        return Component.text()
                .append(Component.text(" - ", TextColor.fromHexString("#E0B0FF")))
                .append(Component.text(name + " ", WHITE, BOLD)
                        .hoverEvent(HoverEvent.showText(Component.text().append(Component.text("Last seen: " + lastJoin + "\n" + uuid)))))
                .append(Component.text("[Accept]", BLUE, BOLD)
                        .hoverEvent(HoverEvent.showText(Component.text("Click here to add " + name + " as a member.")))
                        .clickEvent(ClickEvent.runCommand("/ml add " + name)))
                .build();
    }

    public Component nowAMember(String name) {
        return Component.text()
                .append(Component.text("[", GRAY))
                .append(Component.text("MauveList", TextColor.fromHexString("#5B4253")))
                .append(Component.text("] ", GRAY))
                .append(Component.text(name, WHITE, BOLD))
                .append(Component.text(" is now a member!"))
                .build();
    }

    public Component couldNotAdd() {
        return Component.text()
                .append(Component.text("[", GRAY))
                .append(Component.text("MauveList", TextColor.fromHexString("#5B4253")))
                .append(Component.text(" ]", GRAY))
                .append(Component.text("Could not add member, check member-group in config!", WHITE))
                .build();
    }

    public Component reloaded() {
        return Component.text()
                .append(Component.text("[", GRAY))
                .append(Component.text("MauveList", TextColor.fromHexString("#5B4253")))
                .append(Component.text(" ]", GRAY))
                .append(Component.text("Configuration reloaded.", WHITE))
                .build();
    }

    // Discord Messages

    public MessageCreateData applicationUserIntro() {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getApplicationIntroduction())
                .setColor(2664261)
                .build());
        builder.setComponents(ActionRow.of(Button.success("applicationStart", "Start Application"), Button.danger("cancel", "Cancel")));
        return builder.build();
    }

    public MessageCreateData applicationUserQuestion(String question) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(question)
                .setColor(161240)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationUserSkin(String username) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("Your Minecraft username is: `" + username + "`")
                .setDescription("Is the skin below your Minecraft skin?")
                .setImage("https://minotar.net/armor/body/" + Bukkit.getOfflinePlayer(username).getUniqueId() + "/100.png")
                .setFooter("Please choose an option below.")
                .setColor(161240)
                .build());
        builder.setComponents(ActionRow.of(Button.success("acceptSkin", "This is me"), Button.danger("rejectSkin", "This is not me")));
        return builder.build();
    }

    public MessageCreateData applicationError(String message) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("*" + message + "*")
                .setColor(14242639)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationUserComplete() {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getApplicationCompletion())
                .setColor(2664261)
                .build());
        return builder.build();
    }

    public MessageCreateData application(User user, ArrayList<String> answers) {
        MessageCreateBuilder builder = new MessageCreateBuilder();

        long epoch = System.currentTimeMillis() / 1000;
        String description = "**@here, " + user.getName() + " has applied for the server. Here is their application:**\n\n" +
                "Discord username: " + user.getAsMention() + "\n" +
                "Minecraft username: `" + answers.get(0) + "`\n" +
                "Time submitted: <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*\n\n" +
                "**Question responses:**\n\n";

        ArrayList<String> questions = mauveList.getConfigML().getQuestions();
        for (int i = 1; i < questions.size(); i++)
            description = description.concat("*" + questions.get(i) + "*\n" + answers.get(i) + "\n\n");

        description = description.concat("Please click **Accept** or **Reject** below.");

        builder.setEmbeds(new EmbedBuilder().setAuthor("Applicant: " + user.getName() + " \uD83D\uDCE5", null, user.getAvatarUrl())
                .setDescription(description)
                .setThumbnail("https://minotar.net/armor/body/" + Bukkit.getOfflinePlayer(answers.get(0)).getUniqueId() + "/100.png")
                .setColor(161240)
                .build());

        builder.setComponents(ActionRow.of(Button.primary("applicationAccept:" + user.getId(), "Accept"), Button.secondary("applicationReject:" + user.getId(), "Reject")));
        return builder.build();
    }

    public MessageCreateData applicationAccepted(User user, String minecraftUsername, User acceptor) {
        long epoch = System.currentTimeMillis() / 1000;
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setAuthor("Application accepted for " + user.getName() + " ✅", null, user.getAvatarUrl())
                .setDescription("`" + minecraftUsername + "` is now a member.\n\n" +
                        "Accepted by " + acceptor.getAsMention() + " on <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*.")
                .setColor(2664261)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationRejectReason(User user) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setAuthor("What is the reason for rejecting " + user.getName() + "?", null, user.getAvatarUrl())
                .setColor(14242639)
                .build());
        builder.setComponents(ActionRow.of(Button.secondary("rejectNoReason:" + user.getId(), "No Reason")));
        return builder.build();
    }

    public MessageCreateData applicationRejected(User user, String minecraftUsername, User rejector, String reason) {
        long epoch = System.currentTimeMillis() / 1000;
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setAuthor("Application rejected for " + user.getName() + " ⛔", null, user.getAvatarUrl())
                .setDescription("`" + minecraftUsername + "` will not be added as a member, application discarded.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "Rejected by " + rejector.getAsMention() + " on <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*.")
                .setColor(14242639)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationUserAccepted() {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getAccepted())
                .setColor(2664261)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationUserRejected(String reason) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getRejected() +
                        "\n\nReason: " + reason)
                .setColor(14242639)
                .build());
        return builder.build();
    }

    public MessageCreateData applicationFailed(String reason) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("Application failed")
                .setDescription("Reason: " + reason)
                .setColor(14242639)
                .build());
        return builder.build();
    }
}
