package net.mattlabs.mauvelist.discord.config;

import net.mattlabs.mauvelist.common.config.sections.Connection;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Represents MauveList Discord bot's configuration file, saved to the program data folder as {@code config.conf} with
 * HOCON formatting.
 *
 * <p>This class is serialized into the config file and deserialized from the config file via Configurate. On program
 * load, either the config is created using the default field values of this file, or they are set using the values
 * in the existing config file.</p>
 *
 * <p>The public methods of this class provide the config values once loaded.</p>
 */
@SuppressWarnings({"FieldMayBeFinal"})
@ConfigSerializable
public class Config {

    @Setting(value = "_mattIsAwesome")
    @Comment("""
            MauveList Discord Bot Configuration
            By Mattboy9921
            https://github.com/mattboy9921/MauveList""")
    private boolean _mattIsAwesome = true;

    @Comment("\n** Connection Configuration Settings **")
    private Connection connection = new Connection();

    public Connection getConnection() {
        return connection;
    }

    @Comment("\nThe bot token you will use for applications")
    private String botToken = "paste-your-bot-token-here";

    public String getBotToken() {
        return botToken;
    }
}
