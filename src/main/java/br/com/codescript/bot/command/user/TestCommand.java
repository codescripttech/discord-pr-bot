package br.com.codescript.bot.command.user;

import br.com.codescript.bot.command.Command;
import br.com.codescript.bot.command.model.CommandEvent;
import br.com.codescript.bot.manager.ChatManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCommand implements Command {

    @Override
    public void execute(CommandEvent commandEvent) {
        var user = commandEvent.getUser();
        ChatManager chatManager = new ChatManager(commandEvent.getChannel());
        chatManager.sendAndDelete(user, "SIIMMBOOORAAA!!", 5);
    }
}
