package br.com.codescript.bot.model;

import br.com.codescript.bot.type.Branch;
import br.com.codescript.bot.type.Priority;
import br.com.codescript.bot.type.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

@AllArgsConstructor
@Getter @Setter
@Builder
public class PullRequest {

    private String id;

    private String task;

    private User developer;
    private User reviewer;

    private User scrumMaster;
    private User manager;

    private String url;

    private Priority priority;

    private Status status;

    private Branch branch;

    private ThreadChannel threadChannel;

}
