package br.com.codescript.bot;

import br.com.codescript.bot.configuration.CodeConfiguration;
import br.com.codescript.bot.manager.JDAManager;

public class CodeScriptBot {

    public static void main(String[] args) {
        CodeConfiguration.init();
        JDAManager.init();
    }
}