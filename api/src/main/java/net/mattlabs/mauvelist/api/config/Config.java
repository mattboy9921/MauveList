package net.mattlabs.mauvelist.api.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Represents MauveList's configuration file, saved to the plugin data folder as {@code config.conf} with HOCON
 * formatting.
 *
 * <p>This class is serialized into the config file and deserialized from the config file via Configurate. On program
 * load, either the config is created using the default field values of this file, or they are set using the values
 * in the existing config file.</p>
 *
 * <p>The public methods of this class provide the config values once loaded.</p>
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public class Config {

    @SuppressWarnings("unused")
    @Setting(value = "_mattIsAwesome")
    @Comment("""
            MauveList API Configuration
            By Mattboy9921
            https://github.com/mattboy9921/MauveList""")
    private boolean _mattIsAwesome = true;
}
