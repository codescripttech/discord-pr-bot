package br.com.codescript.bot.command.model;

import lombok.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Getter @Setter
public class CommandEvent {

    private User user;
    private MessageChannelUnion channel;

    private String[] args;

    @Getter @Setter
    private SlashCommandInteractionEvent event;

    public CommandEvent(SlashCommandInteractionEvent event) {
        this.event = event;
        this.user = event.getUser();
        this.channel = event.getChannel();
    }

    public User getOptionAsUser(String option) {
        return this.event.getOption(option).getAsUser();
    }

    public String getOptionAsString(String option) {
        return this.event.getOption(option).getAsString();
    }
}
