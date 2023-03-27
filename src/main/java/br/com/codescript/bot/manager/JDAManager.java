package br.com.codescript.bot.manager;

import br.com.codescript.bot.command.listener.CommandListener;
import br.com.codescript.bot.configuration.CodeConfiguration;
import br.com.codescript.bot.listener.MessageReactionListener;
import br.com.codescript.bot.proxy.PullRequestProxy;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class JDAManager {

    private static JDA instance;

    private JDAManager() {
    }

    @SneakyThrows
    public static void init() {
        var token = CodeConfiguration.getString("token");
        var pullRequestProxy = new PullRequestProxy();
        instance = JDABuilder.createDefault(token)
                .addEventListeners(new CommandListener(pullRequestProxy))
                .setActivity(Activity.playing("CodeScript Quest"))
                .build();
        instance.addEventListener(new MessageReactionListener(pullRequestProxy));
        instance.updateCommands().addCommands(
                Commands.slash("test", "Teste"),
                Commands.slash("pullrequest", "Criar uma nova solicitação de review do Pull Request")
                        .addOption(OptionType.STRING, "task", "Número da task deste Pull Request", true)
                        .addOptions(
                                new OptionData(OptionType.STRING, "branch", "Branch do Pull Request", true)
                                        .addChoice("Merging", "MERGING")
                                        .addChoice("Develop", "DEVELOP")
                        )
                        .addOption(OptionType.USER, "reviewer", "Desenvolvedor que irá fazer a revisão", true)
                        .addOption(OptionType.USER, "scrum-master", "Scrum Master responsável por sua equipe", true)
                        .addOption(OptionType.USER, "manager", "Gerente do projeto responsável pelo monitoramento", true)
                        .addOptions(
                                new OptionData(OptionType.STRING, "priority", "Prioridade do Pull Request", true)
                                        .addChoice("Normal", "NORMAL")
                                        .addChoice("Alta", "HIGH")
                        )
                        .addOption(OptionType.STRING, "url", "URL do Pull Request a ser revisado", true)
        ).queue();
    }

    public static JDA getInstance() {
        return instance;
    }
}
