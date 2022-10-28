package net.mattlabs.mauvelist.listeners;


import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.ApplicationManager;

import java.util.logging.Logger;

public class JDAListener extends ListenerAdapter {

    private final boolean debug = MauveList.getInstance().getConfigML().isDebug();
    private final Logger logger = MauveList.getInstance().getLogger();
    private ApplicationManager applicationManager = MauveList.getInstance().getApplicationManager();

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton().getId().equals("apply")) {
            event.deferEdit().queue();
            applicationManager.create(event.getUser(), event.getInteraction());
        }
        else if (event.getButton().getId().equals("applicationStart")) {
            event.deferEdit().queue();
            applicationManager.update(event.getUser());
        }
        else if (event.getButton().getId().contains("acceptSkin")) {
            event.deferEdit().queue();
            applicationManager.update(event.getUser(), event.getUser().getId());
        }
        else if (event.getButton().getId().contains("rejectSkin")) {
            event.deferEdit().queue();
            applicationManager.update(event.getUser(), null);
        }
        else if (event.getButton().getId().contains("applicationAccept")) {
            event.deferEdit().queue();
            String[] parts = event.getButton().getId().split(":");
            applicationManager.accept(MauveList.getInstance().getJda().retrieveUserById(parts[1]).complete(), event.getUser());
        }
        else if (event.getButton().getId().contains("applicationReject")) {
            event.deferEdit().queue();
            String[] parts = event.getButton().getId().split(":");
            applicationManager.review(MauveList.getInstance().getJda().retrieveUserById(parts[1]).complete(), event.getUser());
        }
        else if (event.getButton().getId().contains("rejectNoReason")) {
            event.deferEdit().queue();
            String[] parts = event.getButton().getId().split(":");
            applicationManager.reject(MauveList.getInstance().getJda().retrieveUserById(parts[1]).complete(), event.getUser(), null);
        }
        if (debug) logger.info(event.getUser().getName() + " pressed button with ID: " + event.getButton().getId());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (applicationManager.hasApplication(event.getAuthor())) {
                applicationManager.update(event.getAuthor(), event.getMessage().getContentDisplay());
            }
            else if (applicationManager.isReviewing(event.getAuthor())) {
                applicationManager.reject(event.getAuthor(), event.getMessage().getContentDisplay());
            }
        }
        if (debug) logger.info("Bot received a message from " + event.getAuthor().getName() + " with content: " + event.getMessage().getContentDisplay());
    }
}
