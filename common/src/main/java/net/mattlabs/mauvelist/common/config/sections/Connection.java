package net.mattlabs.mauvelist.common.config.sections;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class Connection {

    public Connection() {}

    @Comment("\nThe hostname or IP address of the MauveList server.")
    private String hostname = "localhost";

    public String getHostname() {
        return hostname;
    }

    @Comment("\nThe port for the Mauvelist server.")
    private int port = 8080;

    public int getPort() {
        return port;
    }
}
