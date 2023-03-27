package br.com.codescript.bot.util;

import br.com.codescript.bot.model.PullRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class EmbedUtil {

    public static EmbedBuilder getPullRequestEmbed(PullRequest pullRequest) {
        var developer = pullRequest.getDeveloper();
        var reviewer = pullRequest.getReviewer();
        return new EmbedBuilder()
                .setTitle(String.format(":link: Solicitação de Review: %s", pullRequest.getTask()),  pullRequest.getUrl())
                .addField(":bust_in_silhouette: Desenvolvedor", developer.getName(), true)
                .addField(":man_judge: Reviewer", reviewer.getName(), true)
                .addField(":technologist: Scrum Master", pullRequest.getScrumMaster().getName(), true)
                .addField(":gear: Branch", pullRequest.getBranch().getName(), true)
                .addField(":triangular_flag_on_post: Prioridade", pullRequest.getPriority().getName(), true)
                .addField(":hourglass: Status", pullRequest.getStatus().getMessage(), true)
                .setColor(Color.getColor("#0497EB"))
                .setFooter(developer.getName(), developer.getAvatarUrl());
    }
}
