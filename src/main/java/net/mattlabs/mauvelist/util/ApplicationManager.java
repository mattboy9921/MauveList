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
    public void newApplication(User user) {
        // Create new application
        applications.put(user, new Application());
        // Message user to start application
        user.openPrivateChannel().complete().sendMessage(messages.applicationUserIntro()).queue();
    }

    // Called when user clicks the "Start Application" button, starts the application process
    public void startApplication(User user) {
        if (applications.containsKey(user)) {
            if (applications.get(user).getState().equals(Application.State.NOT_STARTED)) {
                applications.get(user).setState(Application.State.USERNAME);
                // Send first question
                String question = config.getQuestions().get(0);
                user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(question)).queue();
            }
        }
    }

    // Called when the user sends a response to the last question, sends next question
    public void update(User user, String answer) {
        if (applications.containsKey(user)) {
            
            Application application = applications.get(user);

            // Username validation
            if (application.getState().equals(Application.State.USERNAME)) {
                if (minecraftUsernameIsValid(answer)) {
                    // Check if Minecraft username already in members group
                    if (!MauveList.getPermission().playerInGroup(null, Bukkit.getOfflinePlayer(answer), config.getMemberGroup())) {
                        user.openPrivateChannel().complete().sendMessage(messages.applicationUserAvatar(answer)).queue();
                        application.setState(Application.State.SKIN);
                    }
                    else {
                        String message = "This Minecraft username is already a member on this server. If you believe this is an error, contact a moderator.";
                        user.openPrivateChannel().complete().sendMessage(messages.applicationError(message)).queue();
                    }
                }
                else {
                    String message = "The minecraft username you have provided is invalid and/or does not exist. Please type out only your username exactly as it shows in game.";
                    user.openPrivateChannel().complete().sendMessage(messages.applicationError(message)).queue();
                }
            }

            // Check if skin correct
            else if (application.getState().equals(Application.State.SKIN)) {
                // Correct skin
                if (user.getId().equals(answer)) {
                    application.setState(Application.State.IN_PROGRESS);
                }
                // Incorrect skin
                else {
                    application.setState(Application.State.USERNAME);
                    // Ask for username again
                    String question = config.getQuestions().get(0);
                    user.openPrivateChannel().complete().sendMessage(messages.applicationUserQuestion(question)).queue();
                }
            }

            // Other questions
            if (application.getState().equals(Application.State.IN_PROGRESS)) {
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
        return username.length() >= 3 && username.length() <= 16 && username.matches("\\w+") && validateMinecraftUsernameWithAPI(username);
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
        private int questionStep = 0;
        private ArrayList<String> answers = new ArrayList<>();
        private boolean waitingForReason = false;
        private User rejector = null;

        private Message applicationMessage = null;

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
