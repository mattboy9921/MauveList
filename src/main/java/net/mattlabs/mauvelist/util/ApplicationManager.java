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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ApplicationManager {

    private MauveList mauveList = MauveList.getInstance();
    private JDA jda = mauveList.getJda();
    private Map<User, Application> applications = new HashMap<>();

    public void add(User user) {
        applications.put(user, new Application());
        user.openPrivateChannel().complete().sendMessage(buildIntro()).queue();
    }

    public void update(User user) {
        if (applications.containsKey(user)) {
            if (applications.get(user).getStep() == -1) {
                applications.get(user).incrementStep();
                user.openPrivateChannel().complete().sendMessage(buildQuestion(applications.get(user).getStep())).queue();
            }
        }
    }

    public void update(User user, String answer) {
        if (applications.containsKey(user)) {
            applications.get(user).getAnswers().add(answer);
            applications.get(user).incrementStep();
            // Send user next step of application
            if (applications.get(user).getStep() < mauveList.getConfigML().getQuestions().size())
                user.openPrivateChannel().complete().sendMessage(buildQuestion(applications.get(user).getStep())).queue();
            // Post application
            else {
                user.openPrivateChannel().complete().sendMessage(buildComplete()).queue();
                // Add and remove @here for ping
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage("@here").queue(message -> message.delete().queue());
                // Send application
                jda.getTextChannelById(mauveList.getConfigML().getApplicationChannel()).sendMessage(buildApplication(user)).queue();
            }
        }
    }

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

    public void rejectStart(User user, Message message, User rejector) {
        message.editMessageComponents(ActionRow.of(Button.secondary("disabled" + user.getId(), "Accept").asDisabled(), Button.danger("disabled", "Rejected").asDisabled())).queue();
        applications.get(user).setWaitingForReason(true);
        applications.get(user).setRejector(rejector);
        rejector.openPrivateChannel().complete().sendMessage(buildRejectReason(rejector)).queue();
    }

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
        for (int i = 0; i < questions.size(); i++)
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

        private int step = -1;
        private ArrayList<String> answers = new ArrayList<>();
        private boolean waitingForReason = false;
        private User rejector = null;

        public int getStep() {
            return step;
        }

        public void incrementStep() {
            step++;
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
    }
}
