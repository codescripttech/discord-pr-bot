package br.com.codescript.bot.service;

import br.com.codescript.bot.configuration.CodeConfiguration;
import br.com.codescript.bot.manager.ChatManager;
import br.com.codescript.bot.manager.GuildManager;
import br.com.codescript.bot.model.PullRequest;
import br.com.codescript.bot.proxy.PullRequestProxy;
import br.com.codescript.bot.type.Branch;
import br.com.codescript.bot.type.Priority;
import br.com.codescript.bot.type.Status;
import br.com.codescript.bot.util.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Arrays;
import java.util.regex.Pattern;

public class PullRequestService {

    private final PullRequestProxy proxy;

    private final Pattern PATTERN = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?github\\.com\\/[a-zA-Z0-9_-]+\\/[a-zA-Z0-9_-]+(?:\\/.*)?$");
    private String guildId;

    public PullRequestService(PullRequestProxy proxy) {
        this.proxy = proxy;
        this.guildId = CodeConfiguration.getString("code_server_id");
    }

    public PullRequest create(ChatManager chatManager, String task, Branch branch, User developer, User reviewer, User scrumMaster, User manager, String url, Priority priority, SlashCommandInteractionEvent event) {
        PullRequest pullRequest = PullRequest.builder()
                .task(task)
                .branch(branch)
                .developer(developer)
                .reviewer(reviewer)
                .scrumMaster(scrumMaster)
                .manager(manager)
                .url(url)
                .priority(priority)
                .status(Status.OPENED)
                .build();

        var embed = EmbedUtil.getPullRequestEmbed(pullRequest);

        event.replyEmbeds(embed.build())
                .addContent(String.format("%s, %s", developer.getAsMention(), reviewer.getAsMention()))
                .addActionRow(
                        Status.IN_REVIEW.getButton()
                )
                .queue(message -> {
                    pullRequest.setId(message.getInteraction().getId());

                    if (priority == Priority.HIGH) {
                        sendPrivateMessage(reviewer, "Há uma solicitação de revisão com prioridade alta no canal de Pull Requests.");
                    }
                    this.proxy.set(pullRequest);
        });
        return pullRequest;
    }

    public boolean verifyURL(User user, ChatManager chatManager, String url) {
        if (!PATTERN.matcher(url).matches()) {
            chatManager.sendAndDelete(user , "Esta link de Pull Request não é válido", 3);
            return false;
        }
        return true;
    }

    public void applyChanges(PullRequest pullRequest, Guild guild, Message message, Channel channel) {
        var developer = pullRequest.getDeveloper();
        var manager = pullRequest.getManager();
        var channelId = channel.getId();
        var messageId = message.getId();
        String task = pullRequest.getTask();
        Branch branch = pullRequest.getBranch();
        GuildManager guildManager = new GuildManager(guild);
        switch (pullRequest.getStatus()) {
            case REQUESTED_CHANGES -> {
                var threadName = String.format("Alterações-%s-%s", developer.getAsTag(), pullRequest.getReviewer().getAsTag());
                guildManager.createTopic(threadName, channelId, messageId, pullRequest.getDeveloper(), pullRequest.getReviewer(), threadChannel -> {
                    pullRequest.setThreadChannel(threadChannel);
                    sendRequestedChangesToDeveloper(developer, task, threadChannel);
                });
            }
            case REQUEST_CHANGES_MADE -> {
                pullRequest.getThreadChannel().delete().queue();
                pullRequest.setThreadChannel(null);
                sendRequestsChangesToReviewer(pullRequest, message.getJumpUrl());
            }
            case APPROVED -> {
                User scrumMaster = pullRequest.getScrumMaster();
                if (!pullRequest.getReviewer().equals(scrumMaster) && isToDevelop(branch)) {
                    sendPrivateMessage(scrumMaster, String.format("Foi aprovado o Pull Request da task %s. Solicite o deploy da mesma.", task));
                }
            }
            case REQUESTED_DEPLOY -> {
                if (isToDevelop(branch))
                    sendPrivateMessage(manager, String.format("Foi solicitado o deploy da task %s.", task));
            }
            case DEPLOYED -> {
                if (isToDevelop(branch))
                    sendPrivateMessage(manager, String.format("Foi realizado o deploy da task %s.", task));
            }
        }
    }

    private void sendRequestedChangesToDeveloper(User developer, String task, ThreadChannel threadChannel) {
        var embed = new EmbedBuilder()
                .setDescription(String.format("Há revisões a serem feitas na task %s! Consulte [aqui](%s)", task, threadChannel.getJumpUrl()));

        sendPrivateEmbed(developer, embed);
    }

    private void sendRequestsChangesToReviewer(PullRequest pullRequest, String messageLink) {
        var embed = new EmbedBuilder()
                .setDescription(String.format("As revisões solicitadas para %s na task %s foram feitas, faça sua revisão novamente. Confira [aqui](%s)", pullRequest.getDeveloper(), pullRequest.getTask(), messageLink));
        sendPrivateEmbed(pullRequest.getReviewer(), embed);
    }

    private void sendPrivateMessage(User user, String message) {
        user.openPrivateChannel().queue(channel -> {
            ChatManager chatManager = new ChatManager(channel);
            chatManager.send(user, String.format(message));
        });
    }

    private void sendPrivateEmbed(User user, EmbedBuilder embedBuilder) {
        user.openPrivateChannel().queue(channel -> {
            ChatManager chatManager = new ChatManager(channel);
            chatManager.sendEmbedMessage(embedBuilder);
        });
    }

    private boolean isToDevelop(Branch branch) {
        return branch == Branch.DEVELOP;
    }
}
