package net.mattlabs.mauvelist.messaging;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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

    public static BaseComponent[] lastTenMembersHeading() {
        // [MauveList] Last 10 nonmembers:
        return new ComponentBuilder("[")
                    .color(GRAY)
                .append("MauveList")
                    .color(DARK_PURPLE)
                .append("] ")
                    .color(GRAY)
                .append("Last 10 nonmembers:")
                    .color(WHITE)
                .create();
    }

    public static BaseComponent[] lastTenMembersListing(String name, String uuid, String lastJoin) {
        //  - %name% <hover UUID> [Accept]
        return new ComponentBuilder(" - ")
                    .color(DARK_PURPLE)
                .append(name + " ")
                    .color(WHITE)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Last seen: " + lastJoin +
                                    "\n" + uuid)
                                    .create()))
                .append("[Accept]")
                    .color(BLUE)
                    .bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click here to add " + name + " as a member.").create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/placeholder"))
                .create();
    }

}
