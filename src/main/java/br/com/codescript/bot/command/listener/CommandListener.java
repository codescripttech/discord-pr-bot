package br.com.codescript.bot.command.listener;

import br.com.codescript.bot.command.Command;
import br.com.codescript.bot.command.model.CommandEvent;
import br.com.codescript.bot.command.user.PullRequestCommand;
import br.com.codescript.bot.command.user.TestCommand;
import br.com.codescript.bot.proxy.PullRequestProxy;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CommandListener extends ListenerAdapter {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandListener(PullRequestProxy proxy) {
        this.init(proxy);
    }

    private void init(PullRequestProxy proxy) {
        System.out.println("Registrando comandos...");
        commandMap.put("test", new TestCommand());
        commandMap.put("pullrequest", new PullRequestCommand(proxy));
    }

    public Optional<Command> getCommand(String command) {
        return Optional.ofNullable(commandMap.get(command.toLowerCase()));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply();
        var commandOptional = getCommand(event.getName());
        commandOptional.ifPresent(command -> command.execute(new CommandEvent(event)));
    }
}
