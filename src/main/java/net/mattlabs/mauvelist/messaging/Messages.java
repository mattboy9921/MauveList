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
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ml add " + name))
                .create();
    }

    public static BaseComponent[] nowAMember(String name) {
        // [MauveList] %name% is now a member!
        return new ComponentBuilder("[")
                    .color(GRAY)
                .append("MauveList")
                    .color(DARK_PURPLE)
                .append("] ")
                    .color(GRAY)
                .append(name)
                    .color(WHITE)
                    .bold(true)
                .append(" is now a member!")
                    .reset()
                .create();
    }

    public static BaseComponent[] couldNotAdd() {
        // [MauveList] Could not add member, check member-group in config!!
        return new ComponentBuilder("[")
                    .color(GRAY)
                .append("MauveList")
                    .color(DARK_PURPLE)
                .append("] ")
                    .color(GRAY)
                .append("Could not add member, check member-group in config!")
                    .color(WHITE)
                .create();
    }

    public static BaseComponent[] reloaded() {
        // [MauveList] Configuration reloaded.
        return new ComponentBuilder("[")
                    .color(GRAY)
                .append("MauveList")
                    .color(DARK_PURPLE)
                .append("] ")
                    .color(GRAY)
                .append("Configuration reloaded.")
                    .color(WHITE)
                .create();
    }
}
