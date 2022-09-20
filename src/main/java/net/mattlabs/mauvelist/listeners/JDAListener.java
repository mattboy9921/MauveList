package net.mattlabs.mauvelist.listeners;


import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.util.ApplicationManager;

public class JDAListener extends ListenerAdapter {

    private ApplicationManager applicationManager = MauveList.getInstance().getApplicationManager();

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton().getId().equals("apply")) {
            event.deferEdit().queue();
            applicationManager.newApplication(event.getUser());
        }
        else if (event.getButton().getId().equals("applicationStart")) {
            event.deferEdit().queue();
            applicationManager.startApplication(event.getUser());
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
            applicationManager.rejectStart(MauveList.getInstance().getJda().retrieveUserById(parts[1]).complete(), event.getUser());
        }
        else if (event.getButton().getId().contains("rejectNoReason")) {
            event.deferEdit().queue();
            String[] parts = event.getButton().getId().split(":");
            applicationManager.rejectConfirm(MauveList.getInstance().getJda().retrieveUserById(parts[1]).complete(), event.getUser(), "No reason provided.");
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (applicationManager.hasApplication(event.getAuthor())) {
                applicationManager.update(event.getAuthor(), event.getMessage().getContentDisplay());
            }
            else if (applicationManager.hasApplicationRejection(event.getAuthor())) {
                applicationManager.rejectConfirm(event.getAuthor(), event.getMessage().getContentDisplay());
            }
        }
    }
}
