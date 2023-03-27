package br.com.codescript.bot.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.function.Consumer;

public class GuildManager {

    private final Guild guild;

    public GuildManager(String guildId) {
        this.guild = JDAManager.getInstance().getGuildById(guildId);
    }

    public GuildManager(Guild guild) {
        this.guild = guild;
    }

    public void createTopic(String name, String channelId, String messageId, User developer, User reviewer, Consumer<ThreadChannel> consumer) {
        guild.getTextChannelById(channelId).createThreadChannel(name, messageId).queue(threadChannel -> {
            threadChannel.addThreadMember(developer).queue();
            threadChannel.addThreadMember(reviewer).queue();
            consumer.accept(threadChannel);
        });
    }
}
