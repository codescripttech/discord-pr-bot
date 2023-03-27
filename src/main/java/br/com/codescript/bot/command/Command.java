package br.com.codescript.bot.command;

import br.com.codescript.bot.command.model.CommandEvent;

public interface Command {

    void execute(CommandEvent commandEvent);

}