package net.mattlabs.mauvelist.discord.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.mattlabs.mauvelist.common.communication.ClientCommunicationManager;
import net.mattlabs.mauvelist.common.communication.Endpoints;
import net.mattlabs.mauvelist.common.records.ApiRequest;
import net.mattlabs.mauvelist.common.records.DiscordActivityRequest;
import net.mattlabs.mauvelist.discord.MauveListDiscord;

import java.time.Instant;

public class UserActivityListener extends ListenerAdapter {

    private final ClientCommunicationManager clientCommunicationManager;

    public UserActivityListener() {
        clientCommunicationManager = MauveListDiscord.getInstance().getClientCommunicationManager();
    }

    // Messages
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        onUserActivity(event.getAuthor());
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        onUserActivity(event.getAuthor());
    }

    // Reactions
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    // Polls
    @Override
    public void onMessagePollVoteAdd(MessagePollVoteAddEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    @Override
    public void onMessagePollVoteRemove(MessagePollVoteRemoveEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    // Join
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        onUserActivity(event.getUser());
    }

    // Leave

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        onUserActivity(event.getUser());
    }

    // Events
    @Override
    public void onScheduledEventUserAdd(ScheduledEventUserAddEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    @Override
    public void onScheduledEventUserRemove(ScheduledEventUserRemoveEvent event) {
        onUserActivity(event.retrieveUser().complete());
    }

    // Voice
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        onUserActivity(event.getEntity().getUser());
    }

    private void onUserActivity(User user) {
        if (!user.isBot() && !user.isSystem()) {
            DiscordActivityRequest discordActivityRequest = new DiscordActivityRequest(user.getName(), Instant.now());
            ApiRequest<DiscordActivityRequest> request = new ApiRequest<>(Endpoints.discordActivity(user.getIdLong()), discordActivityRequest);

            clientCommunicationManager.sendPostRequest(request);
        }
    }
}
