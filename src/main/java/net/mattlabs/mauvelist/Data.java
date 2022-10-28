package net.mattlabs.mauvelist;

import net.mattlabs.mauvelist.util.MauvePlayerData;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ConfigSerializable
public class Data {

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

            "MauveList Data File\n" +
            "By Mattboy9921\n" +
            "https://github.com/mattboy9921/MauveList\n\n" +

            "This is the data file for MauveList.\n\n" +

            "#######################################################################################################\n\n" +

            "Config version. Do not change this!")
    private int schemaVersion = 0;

    private Map<UUID, MauvePlayerData> mauvePlayerMap = new HashMap<>();

    public Map<UUID, MauvePlayerData> getMauvePlayerMap() {
        return mauvePlayerMap;
    }
}
