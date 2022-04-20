package me.wesley1808.servercore.utils;

// import eu.pb4.placeholders.TextParser;
import net.minecraft.network.chat.Component;

public final class Formatter {

    public static Component parse(String input) {
        // return TextParser.parse(input);
        return Component.literal(input);
    }

    public static String line(String input, int targetLength, boolean isPlayer) {
        if (!isPlayer) {
            return input.replace("%LINE%", "");
        }

        String cleanInput = input.replace("%LINE%", "").replaceAll("<.*?>", "").strip();

        final int length = (targetLength - cleanInput.length()) / 2;
        return input.replace("%LINE%", "<strikethrough>" + " ".repeat(length) + "</strikethrough>");
    }

    public static String page(String input, String command, int page) {
        if (page > 1) {
            input = input.replace("%PREV_PAGE%", command(command.replace("%PAGE_NR%", String.valueOf(page - 1)), "<<"));
        } else {
            input = input.replace("%PREV_PAGE%", "<<");
        }

        return input.replace("%NEXT_PAGE%", command(command.replace("%PAGE_NR%", String.valueOf(page + 1)), ">>"));
    }

    public static String command(String command, String text) {
        return String.format("<click:run_command:'%s'>%s</click>", command, text);
    }
}
