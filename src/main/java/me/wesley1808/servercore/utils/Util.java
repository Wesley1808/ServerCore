package me.wesley1808.servercore.utils;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.wesley1808.servercore.config.tables.CommandConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class Util {

    // Outdated message formatting method.
    // Will be used until there's a better solution that consistently works in snapshots.
    public static String formatString(String string) {
        var builder = new StringBuilder(string);
        for (int index = builder.indexOf("&"); index >= 0; index = builder.indexOf("&", index + 1)) {
            if (matches(builder.charAt(index + 1))) {
                builder.setCharAt(index, '§');
            }
        }
        return builder.toString();
    }

    public static String removeFormatting(String string) {
        var builder = new StringBuilder(string);
        for (int index = builder.indexOf("§"); index >= 0; index = builder.indexOf("§", index + 1)) {
            if (matches(builder.charAt(index + 1))) {
                builder.deleteCharAt(index);
                builder.deleteCharAt(index);
            }
        }
        return builder.toString().strip();
    }

    private static boolean matches(Character c) {
        return "b0931825467adcfelmnor".contains(c.toString());
    }

    public static <T> boolean displayPage(List<T> list, int page, int pageSize, BiConsumer<T, Integer> consumer) {
        int index = pageSize * (page - 1);
        int toIndex = Math.min(index + pageSize, list.size());

        if (toIndex <= index) {
            return false;
        }

        for (T value : list.subList(index, toIndex)) {
            consumer.accept(value, ++index);
        }

        return true;
    }

    public static String createHeader(String title, int targetLength, boolean format) {
        if (!format) {
            return title;
        }

        ChatFormatting color = ChatFormatting.getByName(CommandConfig.LINE_COLOR.get());
        if (color == null) {
            throw new IllegalStateException("Config entry for line color is invalid!");
        }

        return createHeader(title, targetLength, color);
    }

    public static String createHeader(String title, int targetLength, ChatFormatting color) {
        if (!color.isColor()) throw new IllegalStateException("You can only specify colors!");

        String string = String.format("#LINE %s #LINE", title);
        int length = (targetLength - string.replace("#LINE", "").length()) / 2;
        return string.replace("#LINE", String.format("§%c§m%s§r", color.getChar(), " ".repeat(length)));
    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        entries.sort((c1, c2) -> c2.getValue().compareTo(c1.getValue()));
        return entries;
    }

    public static boolean isPlayer(CommandSourceStack source) {
        return source.getEntity() instanceof Player;
    }

    public static CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder, String... suggestions) {
        for (String suggestion : suggestions) {
            builder.suggest(suggestion);
        }

        return builder.buildFuture();
    }
}
