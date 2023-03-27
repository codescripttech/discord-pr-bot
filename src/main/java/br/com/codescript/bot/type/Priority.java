package br.com.codescript.bot.type;

public enum Priority {

    NORMAL("Normal"),
    HIGH("Alta");

    private String name;

    Priority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
