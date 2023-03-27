package br.com.codescript.bot.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ChatManager {

    private MessageChannel channel;

    public ChatManager(MessageChannel channel) {
        this.channel = channel;
    }

    public void send(String message) {
        this.channel.sendMessage(message).queue();
    }

    public void send(User user, String message) {
        this.channel.sendMessage(String.format("%s, %s", user.getAsMention(), message)).queue();
    }

    public void sendAndDelete(String message, int timeSeconds) {
        this.channel.sendMessage(message)
                .queue(m -> m.delete().queueAfter(timeSeconds, TimeUnit.SECONDS));
    }

    public void sendAndDelete(User user, String message, int timeSeconds) {
        this.channel.sendMessage(String.format("%s, %s", user.getAsMention(), message))
                .queue(m -> m.delete().queueAfter(timeSeconds, TimeUnit.SECONDS));
    }

    public void sendEmbedMessage(EmbedBuilder embedBuilder) {
        this.channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendEmbedMessage(EmbedBuilder embedBuilder, Consumer<Message> consumer) {
        this.channel.sendMessageEmbeds(embedBuilder.build()).queue(consumer::accept);
    }

    public void editEmbedMessage(String messageId, EmbedBuilder embedBuilder, Consumer<Message> consumer) {
        this.channel.editMessageEmbedsById(messageId, embedBuilder.build()).queue(consumer::accept);
    }

    public String getChannelId() {
        return this.channel.getId();
    }
}
