package br.com.codescript.bot.listener;

import br.com.codescript.bot.manager.ChatManager;
import br.com.codescript.bot.proxy.PullRequestProxy;
import br.com.codescript.bot.service.PullRequestService;
import br.com.codescript.bot.type.Branch;
import br.com.codescript.bot.type.Status;
import br.com.codescript.bot.util.EmbedUtil;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

public class MessageReactionListener extends ListenerAdapter {
    
    private final PullRequestProxy proxy;

    private final PullRequestService service;

    public MessageReactionListener(PullRequestProxy proxy) {
        this.proxy = proxy;
        this.service = new PullRequestService(proxy);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        var user = event.getUser();
        var channel = event.getChannel();
        var button = event.getButton();
        var interaction = event.getMessage().getInteraction();
        var guild = event.getGuild();
        var interactionId = interaction.getId();

        proxy.get(interactionId).ifPresent(pullRequest -> {
            Status newStatus = Status.getFromButton(button);
            if (newStatus == null  || newStatus == pullRequest.getStatus()) return;

            if (newStatus.isAble(pullRequest, user, button)) {
                pullRequest.setStatus(newStatus);
                MessageEditCallbackAction messageEditCallbackAction = event.editMessageEmbeds(EmbedUtil.getPullRequestEmbed(pullRequest).build());
                var buttons = newStatus.getNextButtons();
                if (buttons.length > 0) {
                    messageEditCallbackAction.setActionRow(newStatus.getNextButtons());
                } else {
                    messageEditCallbackAction.setComponents();
                }

                if (pullRequest.getBranch() == Branch.MERGING && newStatus == Status.APPROVED) {
                    messageEditCallbackAction.setComponents();
                }

                messageEditCallbackAction
                        .queue(message -> {
                            service.applyChanges(pullRequest, guild, event.getMessage(), channel);

                            if (newStatus.isAbleClose(pullRequest.getBranch())) {
                                proxy.remove(pullRequest.getId());
                                return;
                            }

                            proxy.set(pullRequest);
                        });
            } else {
                event.deferReply();
            }
        });
    }
}
