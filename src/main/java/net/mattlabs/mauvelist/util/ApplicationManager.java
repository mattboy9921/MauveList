package net.mattlabs.mauvelist.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.mattlabs.mauvelist.Config;
import net.mattlabs.mauvelist.MauveList;
import net.mattlabs.mauvelist.messaging.Messages;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ApplicationManager {

    private final MauveList mauveList = MauveList.getInstance();
    private final Messages messages = mauveList.getMessages();
    private final Config config = mauveList.getConfigML();
    private final JDA jda = mauveList.getJda();
    private final Map<User, Application> applications = new HashMap<>();

    // Add a new Discord user to the applications map
    public void create(User user) {
        // Create new application
        applications.put(user, new Application());
        // Message user to start application
        user.openPrivateChannel().complete().sendMessage(messages.applicationUserIntro()).queue(
                (message) -> applications.get(user).setIntroMessage(message));
    }

    public void update(User user) {
        update(user, null);
    }

    public void update(User user, String response) {
        switch (applications.get(user).getState()) {
            case NOT_STARTED:
                start(user);
                break;
            case USERNAME:
                username(user, response);
                break;
            case SKIN:
                skin(user, user.getId().equals(response));
                break;
            case IN_PROGRESS:
                question(user, response);
                break;
        }
    }

    // Sends first question
    private void start(User user) {
        Application application = applications.get(user);

        // Update intro message
        application.getIntroMessage().editMessageComponents(ActionRow.of(
                Button.success("applicationStart", "Start Application").asDisabled(),
                Button.secondary("cancel", "Cancel").asDisabled())).queue();
        // Update application
        applications.get(user).setState(Application.State.USERNAME);
        // Send first question
        String question = config.getQuestions().get(0);
        user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(question)).queue();
    }

    // Called for username validation
    private void username(User user, String username) {
        Application application = applications.get(user);

        // Username validation
        if (minecraftUsernameIsValid(username)) {
            // Check if Minecraft username already in members group
            if (!MauveList.getPermission().playerInGroup(null, Bukkit.getOfflinePlayer(username), config.getMemberGroup())) {
                // Send skin check
                user.openPrivateChannel().complete().sendMessage(messages.applicationUserSkin(username)).queue(application::setSkinMessage);
                application.setUsername(username);
                application.setState(Application.State.SKIN);
            }
            else {
                String message = "This Minecraft username is already a member on this server. If you believe this is an error, contact a moderator.";
                user.openPrivateChannel().complete().sendMessage(messages.applicationError(message)).queue();
                user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(config.getQuestions().get(0))).queue();
            }
        }
        else {
            String message = "The minecraft username you have provided is invalid and/or does not exist. Please type out only your username exactly as it shows in game.";
            user.openPrivateChannel().complete().sendMessage(messages.applicationError(message)).queue();
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(config.getQuestions().get(0))).queue();
        }
    }

    // Called when the user clicks a button for the skin confirmation
    private void skin(User user, boolean confirmation) {
        Application application = applications.get(user);

        // Correct skin
        if (confirmation) {
            // Update skin message
            application.getSkinMessage().editMessageComponents(ActionRow.of(
                    Button.success("acceptSkin", "This is me").asDisabled(),
                    Button.secondary("rejectSkin", "This is not me").asDisabled())).queue();
            // Update application
            application.getAnswers().add(application.getUsername());
            application.setState(Application.State.IN_PROGRESS);
            application.incrementQuestionStep();
            // Send next question
            String question = config.getQuestions().get(application.getQuestionStep());
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(question)).queue();
        }
        // Incorrect skin
        else {
            // Update skin message
            application.getSkinMessage().editMessageComponents(ActionRow.of(
                    Button.secondary("acceptSkin", "This is me").asDisabled(),
                    Button.danger("rejectSkin", "This is not me").asDisabled())).queue();
            // Update application
            application.setState(Application.State.USERNAME);
            // Ask for username again
            String question = config.getQuestions().get(0);
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(question)).queue();
        }
    }

    // Called when the user sends a response to the last question, sends next question
    private void question(User user, String answer) {
        Application application = applications.get(user);

        application.getAnswers().add(answer);
        application.incrementQuestionStep();

        // Send user next step of application
        if (application.getQuestionStep() < config.getQuestions().size())
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(config.getQuestions().get(application.getQuestionStep()))).queue();
        // Post application
        else {
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserComplete()).queue();
            // Add and remove @here for ping
            jda.getTextChannelById(config.getApplicationChannel()).sendMessage("@here").queue(message -> message.delete().queue());
            // Send application
            jda.getTextChannelById(config.getApplicationChannel())
                    .sendMessage(messages.application(user, application.getAnswers()))
                    .queue((message) -> application.setApplicationMessage(message));

            application.setState(Application.State.SUBMITTED);
        }
    }

    // Called when a moderator clicks "Accept" on an application
    public void accept(User user, User acceptor) {
        Application application = applications.get(user);

        // Edit application message buttons
        application.getApplicationMessage().editMessageComponents(ActionRow.of(
                Button.success("disabled" + user.getId(), "Accepted").asDisabled(),
                Button.secondary("disabled", "Reject").asDisabled())).queue();

        // Add Minecraft user
        Bukkit.getScheduler().runTask(mauveList, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ml add " + application.answers.get(0));

            Bukkit.getScheduler().runTaskAsynchronously(mauveList, () -> {
                // Update applications channel
                jda.getTextChannelById(config.getApplicationChannel()).sendMessage(messages.applicationAccepted(user, application.getAnswers().get(0), acceptor)).queue();
                // Update user
                user.openPrivateChannel().complete().sendMessage(messages.applicationUserAccepted()).queue();

                applications.remove(user);
            });
        });

    }

    // Called when a moderator clicks "Reject" on an application, sends mod DM for reason
    public void rejectStart(User user, User rejector) {
        Application application = applications.get(user);

        // Edit application message footer/buttons
        application.getApplicationMessage().editMessageEmbeds(new EmbedBuilder(application.getApplicationMessage().getEmbeds().get(0))
                .setFooter("Being reviewed by " + user.getName(), user.getAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
        application.getApplicationMessage().editMessageComponents(ActionRow.of(
                Button.secondary("disabled" + user.getId(), "Accept").asDisabled(),
                Button.danger("disabled", "Rejected").asDisabled())).queue();

        // Update application
        application.setWaitingForReason(true);
        application.setRejector(rejector);
        application.setState(Application.State.UNDER_REVIEW);

        // Ask rejector for reason
        rejector.openPrivateChannel().complete().sendMessage(messages.applicationRejectReason(rejector)).queue();
    }

    // Called when mod responds with reason or clicks no reason
    public void rejectConfirm(User rejector, String reason) {
        User user = null;
        for (Entry<User, Application> entry : applications.entrySet())
            if (entry.getValue().isWaitingForReason())
                if (entry.getValue().getRejector().equals(rejector))
                    user = entry.getKey();
        rejectConfirm(user, rejector, reason);
    }

    public void rejectConfirm(User user, User rejector, String reason) {
        Application application = applications.get(user);

        // Edit application message footer
        application.getApplicationMessage().editMessageEmbeds(new EmbedBuilder(application.applicationMessage.getEmbeds().get(0)).setFooter(null).build()).queue();

        try {
            jda.getTextChannelById(config.getApplicationChannel()).sendMessage(messages.applicationRejected(user, application.getAnswers().get(0), rejector, reason)).queue();
            user.openPrivateChannel().complete().sendMessage(messages.applicationUserRejected(reason)).queue();
            applications.remove(user);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            jda.getTextChannelById(config.getApplicationChannel()).sendMessage(messages.applicationFailed(e.getMessage())).queue();
        }
    }

    private boolean minecraftUsernameIsValid(String username) {
        return username != null && username.length() >= 3 && username.length() <= 16 && username.matches("\\w+") && validateMinecraftUsernameWithAPI(username);
    }

    private boolean validateMinecraftUsernameWithAPI(String username){
        String urlString = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500);
            connection.setReadTimeout(500);

            int statusCode = connection.getResponseCode();

            return statusCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasApplication(User user) {
        return applications.containsKey(user);
    }

    public boolean hasApplicationRejection(User user) {
        boolean appRejection = false;
        for (Application application : applications.values())
            if (application.isWaitingForReason())
                if (application.getRejector().equals(user))
                    appRejection = true;
        return appRejection;
    }



    private static class Application {

        private State state = State.NOT_STARTED;
        private String username = null;
        private int questionStep = 0;
        private ArrayList<String> answers = new ArrayList<>();
        private boolean waitingForReason = false;
        private User rejector = null;
        private Message introMessage = null;
        private Message skinMessage = null;
        private Message applicationMessage = null;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public int getQuestionStep() {
            return questionStep;
        }

        public void incrementQuestionStep() {
            questionStep++;
        }

        public void decrementQuestionStep() {
            questionStep--;
        }

        public ArrayList<String> getAnswers() {
            return answers;
        }

        public boolean isWaitingForReason() {
            return waitingForReason;
        }

        public void setWaitingForReason(boolean waitingForReason) {
            this.waitingForReason = waitingForReason;
        }

        public User getRejector() {
            return rejector;
        }

        public void setRejector(User rejector) {
            this.rejector = rejector;
        }

        public Message getIntroMessage() {
            return introMessage;
        }

        public void setIntroMessage(Message introMessage) {
            this.introMessage = introMessage;
        }

        public Message getSkinMessage() {
            return skinMessage;
        }

        public void setSkinMessage(Message skinMessage) {
            this.skinMessage = skinMessage;
        }

        public Message getApplicationMessage() {
            return applicationMessage;
        }

        public void setApplicationMessage(Message applicationMessage) {
            this.applicationMessage = applicationMessage;
        }

        public enum State {
            NOT_STARTED,
            USERNAME,
            SKIN,
            IN_PROGRESS,
            SUBMITTED,
            UNDER_REVIEW
        }
    }
}
