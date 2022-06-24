package net.mattlabs.mauvelist;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Arrays;

@ConfigSerializable
public class Config {

    // Header fields
    @Setting(value = "_schema-version")
    @Comment("#######################################################################################################\n" +
            "     _____ ______   ________  ___  ___  ___      ___ _______   ___       ___  ________  _________   \n" +
            "   |\\   _ \\  _   \\|\\   __  \\|\\  \\|\\  \\|\\  \\    /  /|\\  ___ \\ |\\  \\     |\\  \\|\\   ____\\|\\___   ___\\ \n" +
            "   \\ \\  \\\\\\__\\ \\  \\ \\  \\|\\  \\ \\  \\\\\\  \\ \\  \\  /  / | \\   __/|\\ \\  \\    \\ \\  \\ \\  \\___|\\|___ \\  \\_| \n" +
            "    \\ \\  \\\\|__| \\  \\ \\   __  \\ \\  \\\\\\  \\ \\  \\/  / / \\ \\  \\_|/_\\ \\  \\    \\ \\  \\ \\_____  \\   \\ \\  \\  \n" +
            "     \\ \\  \\    \\ \\  \\ \\  \\ \\  \\ \\  \\\\\\  \\ \\    / /   \\ \\  \\_|\\ \\ \\  \\____\\ \\  \\|____|\\  \\   \\ \\  \\ \n" +
            "      \\ \\__\\    \\ \\__\\ \\__\\ \\__\\ \\_______\\ \\__/ /     \\ \\_______\\ \\_______\\ \\__\\____\\_\\  \\   \\ \\__\\\n" +
            "       \\|__|     \\|__|\\|__|\\|__|\\|_______|\\|__|/       \\|_______|\\|_______|\\|__|\\_________\\   \\|__|\n" +
            "                                                                               \\|_________|        \n\n" +

            "MauveList Configuration\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/MauveList\n\n" +

            "This is the main configuration file for MauveList.\n\n" +

            "#######################################################################################################\n\n" +

            "Config version. Do not change this!")
    private int schemaVersion = 0;

    @Comment("\nThe group to set nonmembers to when added (the members group)\n")
    private String memberGroup = "mauve";

    public String getMemberGroup() {
        return memberGroup;
    }

    @Comment("\nWhether to set the player to the new group or just add them\n" +
            "Possible values: set, add")
    private String permissionType = "set";

    public String getPermissionType() {
        return permissionType;
    }

    @Comment("\nThe channel where players will see the application info and button")
    private String applyChannel = "000000000000000000";

    public String getApplyChannel() {
        return applyChannel;
    }

    @Comment("\nThe channel where submitted applications will be posted")
    private String applicationChannel = "000000000000000000";

    public String getApplicationChannel() {
        return applicationChannel;
    }

    @Comment("\nThe message title to show in the apply channel")
    private String applyTitle = "Application Info";

    public String getApplyTitle() {
        return applyTitle;
    }

    @Comment("\nThe message body to show in the apply channel")
    private String applyBody = "Click the button below to apply.";

    public String getApplyBody() {
        return applyBody;
    }

    @Comment("\nThe bot token you will use for applications")
    private String botToken = "paste-your-bot-token-here";

    public String getBotToken() {
        return botToken;
    }

    @Comment("\nThe introduction message the bot sends to the applicant")
    private String applicationIntroduction = "Hi there, thank you for showing interest in our server. Please click the button below to start the application process.";

    public String getApplicationIntroduction() {
        return applicationIntroduction;
    }

    @Comment("\nThe application questions (\"What is your Minecraft username?\" is asked by default and does not need to be added here)")
    private ArrayList<String> questions = new ArrayList(Arrays.asList("Question one?", "Question two?", "Question three?"));

    public ArrayList<String> getQuestions() {
        ArrayList<String> allQuestions = new ArrayList<>(questions);
        allQuestions.add(0, "What is your Minecraft username? Please type out only your username exactly as it shows in game, this is used to set you up in game.");
        return allQuestions;
    }

    @Comment("\nThe completion message the bot sends after the application questions are answered")
    private String applicationCompletion = "Thank you for your submission. Your application has been sent to the moderation team for review. Look for a DM from this bot if you are accepted.";

    public String getApplicationCompletion() {
        return applicationCompletion;
    }

    @Comment("\nThe message sent to accepted users")
    private String accepted = "Thank you for applying to join the server. We are excited to tell you you've been accepted!";

    public String getAccepted() {
        return accepted;
    }

    @Comment("\nThe message sent to rejected users")
    private String rejected = "Thank you for your interest in the server. Unfortunately, your application has been rejected.";

    public String getRejected() {
        return rejected;
    }
}
