package net.mattlabs.mauvelist.messaging;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

import static net.md_5.bungee.api.ChatColor.*;

public class Messages {

    private Messages() {

    }

    public static BaseComponent[] version() {
        // [MauveList] Version: *version*
        return new ComponentBuilder("[")
                    .color(GRAY)
                .append("MauveList")
                    .color(DARK_PURPLE)
                .append("] ")
                    .color(GRAY)
                .append("Version: " + Bukkit.getPluginManager().getPlugin("MauveList").getDescription().getVersion())
                    .color(WHITE)
                .create();
    }

}
