package net.mattlabs.mauvelist.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.mattlabs.mauvelist.MauveList;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ApplicationManager {

    private final MauveList mauveList = MauveList.getInstance();
    private final JDA jda = mauveList.getJda();
    private final Map<User, Application> applications = new HashMap<>();

    // Add a new Discord user to the applications map
    public void newApplication(User user) {
        // Create new application
        applications.put(user, new Application());
        // Message user to start application
        user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserIntro()).queue();
    }

    // Called when user clicks the "Start Application" button, starts the application process
    public void startApplication(User user) {
        if (applications.containsKey(user)) {
            if (applications.get(user).getState().equals(Application.State.NOT_STARTED)) {
                applications.get(user).setState(Application.State.USERNAME);
                // Send first question
                String question = mauveList.getConfigML().getQuestions().get(0);
                user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserQuestion(question)).queue();
            }
        }
    }

    // Called when the user sends a response to the last question, sends next question
    public void update(User user, String answer) {
        if (applications.containsKey(user)) {

            // Username validation
            if (applications.get(user).getState().equals(Application.State.USERNAME)) {
                if (minecraftUsernameIsValid(answer)) {
                    // Check if Minecraft username already in members group
                    if (!MauveList.getPermission().playerInGroup(null, Bukkit.getOfflinePlayer(answer), mauveList.getConfigML().getMemberGroup())) {
                        user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserAvatar(answer)).queue();
                        applications.get(user).setState(Application.State.SKIN);
                    }
                    else {
                        String message = "This Minecraft username is already a member on this server. If you believe this is an error, contact a moderator.";
                        user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationError(message)).queue();
                    }
                }
                else {
                    String message = "The minecraft username you have provided is invalid and/or does not exist. Please type out only your username exactly as it shows in game.";
                    user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationError(message)).queue();
                }
            }

            // Check if skin correct
            else if (applications.get(user).getState().equals(Application.State.SKIN)) {
                // Correct skin
                if (answer != null) {
                    applications.get(user).getAnswers().add(answer);
                    applications.get(user).setState(Application.State.IN_PROGRESS);
                }
                // Incorrect skin
                else {
                    applications.get(user).setState(Application.State.USERNAME);
                    // Ask for username again
                    String question = mauveList.getConfigML().getQuestions().get(0);
                    user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserQuestion(question)).queue();
                }
            }

            // Other questions
            if (applications.get(user).getState().equals(Application.State.IN_PROGRESS)) {
                applications.get(user).getAnswers().add(answer);
                applications.get(user).incrementQuestionStep();

                // Send user next step of application
                if (applications.get(user).getQuestionStep() < mauveList.getConfigML().getQuestions().size())
                    user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserQuestion(mauveList.getConfigML().getQuestions().get(applications.get(user).getQuestionStep()))).queue();
                    // Post application
                else {
                    user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserComplete()).queue();
                    // Add and remove @here for ping
                    jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage("@here").queue(message -> message.delete().queue());
                    // Send application
                    jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(mauveList.getMessages().application(user, applications.get(user).getAnswers())).queue();

                    applications.get(user).setState(Application.State.SUBMITTED);
                }
            }
        }
    }

    // Called when a moderator clicks "Accept" on an application
    public void accept(User user, Message message, User acceptor) {
        message.editMessageComponents(ActionRow.of(Button.success("disabled" + user.getId(), "Accepted").asDisabled(), Button.secondary("disabled", "Reject").asDisabled())).queue();
        Bukkit.getScheduler().runTask(mauveList, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ml add " + applications.get(user).answers.get(0));
            Bukkit.getScheduler().runTaskAsynchronously(mauveList, () -> {
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(mauveList.getMessages().applicationAccepted(user, applications.get(user).getAnswers().get(0), acceptor)).queue();
                user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserAccepted()).queue();
                applications.remove(user);
            });
        });

    }

    // Called when a moderator clicks "Reject" on an application, sends mod DM for reason
    public void rejectStart(User user, Message message, User rejector) {
        message.editMessageComponents(ActionRow.of(Button.secondary("disabled" + user.getId(), "Accept").asDisabled(), Button.danger("disabled", "Rejected").asDisabled())).queue();
        applications.get(user).setWaitingForReason(true);
        applications.get(user).setRejector(rejector);
        applications.get(user).setState(Application.State.UNDER_REVIEW);
        rejector.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationRejectReason(rejector)).queue();
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
        try {
            jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(mauveList.getMessages().applicationRejected(user, applications.get(user).getAnswers().get(0), rejector, reason)).queue();
            user.openPrivateChannel().complete().sendMessage(mauveList.getMessages().applicationUserRejected(reason)).queue();
            applications.remove(user);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(mauveList.getMessages().applicationFailed(e.getMessage())).queue();
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
