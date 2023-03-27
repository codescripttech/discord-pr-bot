package br.com.codescript.bot.type;

public enum Branch {

    MERGING("Merging"),
    DEVELOP("Develop");

    private String name;

    Branch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}