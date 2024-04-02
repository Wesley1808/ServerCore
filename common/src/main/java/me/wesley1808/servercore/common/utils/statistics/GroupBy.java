package me.wesley1808.servercore.common.utils.statistics;

public enum GroupBy {
    TYPE("Type"),
    PLAYER("Player");

    private final String name;

    GroupBy(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}