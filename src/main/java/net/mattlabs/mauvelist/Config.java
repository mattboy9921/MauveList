package net.mattlabs.mauvelist;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

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

    @Comment("The group to set nonmembers to when added (the members group)\n")
    private String memberGroup = "mauve";

    public String getMemberGroup() {
        return memberGroup;
    }

    @Comment("Whether to set the player to the new group or just add them\n" +
            "Possible values: set, add")
    private String permissionType = "set";

    public String getPermissionType() {
        return permissionType;
    }
}
