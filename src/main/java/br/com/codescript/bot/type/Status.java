package br.com.codescript.bot.type;

import br.com.codescript.bot.model.PullRequest;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;
import java.util.List;

public enum Status {

    OPENED("Aberto", null, null) {
        @Override
        public boolean isAbleStatus(Status status) {
            return true;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getDeveloper();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { IN_REVIEW.getButton() };
        }
    },
    IN_REVIEW("Em revisão", "\uD83D\uDC40", Button.primary("IN_REVIEW", "Iniciar revisão")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == OPENED || status == REQUEST_CHANGES_MADE;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getReviewer();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { REQUESTED_CHANGES.getButton(), APPROVED.getButton() };
        }
    },
    REQUESTED_CHANGES("Alterações solicitadas", "❕", Button.danger("REQUESTED_CHANGES", "Solicitar alterações")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == IN_REVIEW || status == REQUEST_CHANGES_MADE;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getReviewer();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { REQUEST_CHANGES_IN_PROGRESS.getButton()};
        }
    },
    REQUEST_CHANGES_IN_PROGRESS("Alterações em andamento", "\uD83D\uDD04", Button.primary("REQUEST_CHANGES_IN_PROGRESS", "Iniciar alteração")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == REQUESTED_CHANGES;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getDeveloper();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { REQUEST_CHANGES_MADE.getButton()};
        }
    },
    REQUEST_CHANGES_MADE("Alterações realizadas", "\uD83C\uDD99", Button.primary("REQUEST_CHANGES_MADE", "Realizada alterações")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == REQUEST_CHANGES_IN_PROGRESS;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getDeveloper();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { IN_REVIEW.getButton()};
        }
    },
    APPROVED("Aprovado", "✅", Button.primary("APPROVED", "Aprovado")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == IN_REVIEW;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return true;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getReviewer();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { REQUESTED_DEPLOY.getButton() };
        }
    },
    REQUESTED_DEPLOY("Solicitado deploy", "☑️", Button.primary("REQUESTED_DEPLOY", "Solicitado deploy")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == APPROVED;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return branch == Branch.DEVELOP;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getScrumMaster();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] { DEPLOYED.getButton() };
        }
    },
    DEPLOYED("Realizado deploy", "\uD83C\uDFC6", Button.primary("DEPLOYED", "Realizado deploy")) {
        @Override
        public boolean isAbleStatus(Status status) {
            return status == REQUESTED_DEPLOY;
        }

        @Override
        public boolean isAbleBranch(Branch branch) {
            return branch == Branch.DEVELOP;
        }

        @Override
        public User getResponsible(PullRequest pullRequest) {
            return pullRequest.getScrumMaster();
        }

        @Override
        public Button[] getNextButtons() {
            return new Button[] {};
        }
    };

    private String message;
    private Emoji emoji;
    private Button button;

    Status(String message, String emoji, Button button) {
        this.message = message;
        this.emoji = emoji != null ? Emoji.fromUnicode(emoji) : null;
        this.button = button != null ? button.withEmoji(this.emoji) : null;
    }

    public static Status getFromEmoji(Emoji emoji) {
        for (Status value : values()) {
            if (value.getEmoji() != null && value.getEmoji().equals(emoji)) return value;
        }
        return null;
    }

    public static Status getFromButton(Button button) {
        for (Status value : values()) {
            if (value.getButton() != null && value.getButton().equals(button)) return value;
        }
        return null;
    }

    public static List<Button> getAllButtons() {
        return Arrays.stream(values()).filter(status -> status.getButton() != null).map(Status::getButton).toList();
    }

    public boolean isAble(PullRequest pullRequest, User user, MessageReaction messageReaction) {
        return this.verifyReaction(messageReaction.getEmoji()) &&
                this.isAbleBranch(pullRequest.getBranch()) &&
                this.isAbleStatus(pullRequest.getStatus()) &&
                this.isAbleUser(user, pullRequest);
    }

    public boolean isAble(PullRequest pullRequest, User user, Button button) {
        return this.verifyButton(button) &&
                this.isAbleBranch(pullRequest.getBranch()) &&
                this.isAbleStatus(pullRequest.getStatus()) &&
                this.isAbleUser(user, pullRequest);
    }

    public boolean isAbleUser(User user,PullRequest pullRequest) {
        return this.getResponsible(pullRequest).equals(user);
    }

    public boolean verifyReaction(Emoji otherEmoji) {
        return otherEmoji.equals(this.emoji);
    }

    public boolean verifyButton(Button otherButton) {
        return otherButton.equals(this.button);
    }

    public abstract boolean isAbleStatus(Status status);

    public abstract boolean isAbleBranch(Branch branch);

    public abstract User getResponsible(PullRequest pullRequest);

    public abstract Button[] getNextButtons();

    public String getMessage() {
        return message;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public Button getButton() {
        return button;
    }

    public boolean isAbleClose(Branch branch) {
        return (branch == Branch.MERGING && this == APPROVED) || (branch == Branch.DEVELOP && this == DEPLOYED);
    }
}
