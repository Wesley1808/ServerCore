package me.wesley1808.servercore.common.utils.statistics;

public enum StatisticType {
    ENTITY("Entities", "entities"),
    BLOCK_ENTITY("Block Entities", "block-entities");

    private final String name;
    private final String commandFormat;

    StatisticType(String name, String commandFormat) {
        this.name = name;
        this.commandFormat = commandFormat;
    }

    public String getName() {
        return this.name;
    }

    public String getCommandFormat() {
        return this.commandFormat;
    }
}
