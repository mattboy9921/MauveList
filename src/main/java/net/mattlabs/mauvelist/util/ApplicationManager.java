package net.mattlabs.mauvelist.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
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
        user.openPrivateChannel().complete().sendMessage(buildIntro()).queue();
    }

    // Called when user clicks the "Start Application" button, starts the application process
    public void startApplication(User user) {
        if (applications.containsKey(user)) {
            if (applications.get(user).getState().equals(Application.State.NOT_STARTED)) {
                applications.get(user).setState(Application.State.IN_PROGRESS);
                // Send first question
                user.openPrivateChannel().complete().sendMessage(buildQuestion(applications.get(user).getQuestionStep())).queue();
            }
        }
    }

    // Called when the user sends a response to the last question, sends next question
    public void update(User user, String answer) {
        if (applications.containsKey(user)) {
            // Username validation
            if (applications.get(user).getQuestionStep() == 0) {
                if (minecraftUsernameIsValid(answer)) {
                    applications.get(user).getAnswers().add(answer);
                    applications.get(user).incrementQuestionStep();
                } else {
                    user.openPrivateChannel().complete().sendMessage("The minecraft username you have provided is invalid and/or does not exist. \n Please type out only your username exactly as it shows in game.").queue();
                }
            } else {
                applications.get(user).getAnswers().add(answer);
                applications.get(user).incrementQuestionStep();
            }
            // Send user next step of application
            if (applications.get(user).getQuestionStep() < mauveList.getConfigML().getQuestions().size())
                user.openPrivateChannel().complete().sendMessage(buildQuestion(applications.get(user).getQuestionStep())).queue();
            // Post application
            else {
                user.openPrivateChannel().complete().sendMessage(buildComplete()).queue();
                // Add and remove @here for ping
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage("@here").queue(message -> message.delete().queue());
                // Send application
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(buildApplication(user)).queue();

                applications.get(user).setState(Application.State.SUBMITTED);
            }
        }
    }

    // Called when a moderator clicks "Accept" on an application
    public void accept(User user, Message message, User acceptor) {
        message.editMessageComponents(ActionRow.of(Button.success("disabled" + user.getId(), "Accepted").asDisabled(), Button.secondary("disabled", "Reject").asDisabled())).queue();
        Bukkit.getScheduler().runTask(mauveList, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ml add " + applications.get(user).answers.get(0));
            Bukkit.getScheduler().runTaskAsynchronously(mauveList, () -> {
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(buildApplicationSuccess(user, acceptor)).queue();
                user.openPrivateChannel().complete().sendMessage(buildAccepted()).queue();
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
        rejector.openPrivateChannel().complete().sendMessage(buildRejectReason(rejector)).queue();
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
            jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(buildApplicationRejected(user, rejector, reason)).queue();
            user.openPrivateChannel().complete().sendMessage(buildRejected(reason)).queue();
            applications.remove(user);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(buildApplicationFailed(e.getMessage())).queue();
        }
    }

    private Message buildIntro() {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getApplicationIntroduction())
                .setColor(2664261)
                .build());
        builder.setActionRows(ActionRow.of(Button.success("applicationStart", "Start Application"), Button.danger("cancel", "Cancel")));
        return builder.build();
    }

    private Message buildQuestion(int step) {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getQuestions().get(step))
                .setColor(161240)
                .build());
        return builder.build();
    }

    private Message buildComplete() {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getApplicationCompletion())
                .setColor(2664261)
                .build());
        return builder.build();
    }

    private Message buildApplication(User user) {
        MessageBuilder builder = new MessageBuilder();

        long epoch = System.currentTimeMillis() / 1000;
        String description = "**@here, " + user.getName() + " has applied for the server. Here is their application:**\n\n" +
                "Discord username: " + user.getAsMention() + "\n" +
                "Time submitted: <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*\n\n" +
                "**Question responses:**\n\n";

        ArrayList<String> questions = mauveList.getConfigML().getQuestions();
        ArrayList<String> answers = applications.get(user).getAnswers();
        description = description.concat("*" + questions.get(0) + "*\n`" + answers.get(0) + "`\n\n");
        for (int i = 1; i < questions.size(); i++)
            description = description.concat("*" + questions.get(i) + "*\n" + answers.get(i) + "\n\n");

        description = description.concat("Please click **Accept** or **Reject** below.");

        builder.setEmbeds(new EmbedBuilder().setTitle("Applicant: " + user.getName())
                .setDescription(description)
                .setColor(161240)
                .build());

        builder.setActionRows(ActionRow.of(Button.primary("applicationAccept:" + user.getId(), "Accept"), Button.secondary("applicationReject:" + user.getId(), "Reject")));
        return builder.build();
    }

    private Message buildApplicationSuccess(User user, User acceptor) {
        long epoch = System.currentTimeMillis() / 1000;
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("Application accepted for " + user.getName())
                .setDescription(applications.get(user).getAnswers().get(0) + " is now a member.\n\n" +
                        "Accepted by " + acceptor.getAsMention() + " on <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*.")
                .setColor(2664261)
                .build());
        return builder.build();
    }

    private Message buildApplicationFailed(String reason) {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("Application failed")
                .setDescription("Reason:\n" + reason)
                .setColor(14242639)
                .build());
        return builder.build();
    }

    private Message buildRejectReason(User user) {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("What is the reason for rejecting " + user.getName() + "?")
                .setColor(14242639)
                .build());
        builder.setActionRows(ActionRow.of(Button.secondary("rejectNoReason:" + user.getId(), "No Reason")));
        return builder.build();
    }

    private Message buildApplicationRejected(User user, User rejector, String reason) {
        long epoch = System.currentTimeMillis() / 1000;
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle("Application rejected for " + user.getName())
                .setDescription(applications.get(user).getAnswers().get(0) + " will not be added as a member, application discarded.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "Rejected by " + rejector.getAsMention() + " on <t:" + epoch + ":F> *(<t:" + epoch + ":R>)*.")
                .setColor(14242639)
                .build());
        return builder.build();
    }

    private Message buildAccepted() {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getAccepted())
                .setColor(2664261)
                .build());
        return builder.build();
    }

    private Message buildRejected(String reason) {
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbeds(new EmbedBuilder().setTitle(mauveList.getConfigML().getApplyTitle())
                .setDescription(mauveList.getConfigML().getRejected() +
                        "\n\nReason: " + reason)
                .setColor(14242639)
                .build());
        return builder.build();
    }

    private String removePunctuation(String string) {
        return string.replaceAll("[\\p{Punct}&&[^_]]+", "");
    }

    private String getMinecraftUsernameFromString(String usernameString) {
        String[] usernameArray = usernameString.split(" ");
        return usernameArray[usernameArray.length - 1];
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
            IN_PROGRESS,
            SUBMITTED,
            UNDER_REVIEW
        }
    }
}
