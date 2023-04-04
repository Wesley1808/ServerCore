package me.wesley1808.servercore.common.services;

import net.minecraft.network.chat.Component;

public class Formatter {

    public static Component parse(String input) {
        return Platform.INSTANCE.parseText(input);
    }

    public static String line(String input, int targetLength, boolean isPlayer) {
        if (!isPlayer) {
            return input.replace("${line}", "");
        }

        String cleanInput = input.replace("${line}", "").replaceAll("<.*?>", "").strip();

        final int length = (targetLength - cleanInput.length()) / 2;
        return input.replace("${line}", "<strikethrough>" + " ".repeat(length) + "</strikethrough>");
    }

    public static String page(String input, String command, int page) {
        if (page > 1) {
            input = input.replace("${prev_page}", command(command.replace("%page_nr%", String.valueOf(page - 1)), "<<"));
        } else {
            input = input.replace("${prev_page}", "<<");
        }

        return input.replace("${next_page}", command(command.replace("%page_nr%", String.valueOf(page + 1)), ">>"));
    }

    public static String command(String command, String text) {
        return String.format("<click:run_command:'/%s'>%s</click>", command, text);
    }
}
