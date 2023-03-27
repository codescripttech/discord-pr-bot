package br.com.codescript.bot.command.user;

import br.com.codescript.bot.command.Command;
import br.com.codescript.bot.command.model.CommandEvent;
import br.com.codescript.bot.manager.ChatManager;
import br.com.codescript.bot.proxy.PullRequestProxy;
import br.com.codescript.bot.service.PullRequestService;
import br.com.codescript.bot.type.Branch;
import br.com.codescript.bot.type.Priority;

public class PullRequestCommand implements Command {

    private final PullRequestService service;

    public PullRequestCommand(PullRequestProxy proxy) {
        this.service = new PullRequestService(proxy);
    }

    @Override
    public void execute(CommandEvent commandEvent) {
        var developer = commandEvent.getUser();
        var task = commandEvent.getOptionAsString("task");
        var branch = Branch.valueOf(commandEvent.getOptionAsString("branch"));
        var reviewer = commandEvent.getOptionAsUser("reviewer");
        var scrumMaster = commandEvent.getOptionAsUser("scrum-master");
        var manager = commandEvent.getOptionAsUser("manager");
        var chatManager = new ChatManager(commandEvent.getChannel());
        var url = commandEvent.getOptionAsString("url");
        var priority = Priority.valueOf(commandEvent.getOptionAsString("priority"));

        if (!service.verifyURL(developer, chatManager, url)) return;

        service.create(chatManager, task, branch, developer, reviewer, scrumMaster, manager, url, priority, commandEvent.getEvent());
    }
}
