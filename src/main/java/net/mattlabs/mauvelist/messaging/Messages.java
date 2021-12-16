package net.mattlabs.mauvelist.messaging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class Messages {

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
}
