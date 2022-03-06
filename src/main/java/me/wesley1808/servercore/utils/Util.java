package me.wesley1808.servercore.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        return builder.toString();
    }

    private static boolean matches(Character c) {
        return "b0931825467adcfelmnor".contains(c.toString());
    }

    public static <T> boolean displayPage(List<T> list, int page, BiConsumer<T, Integer> consumer) {
        int index = 9 * (page - 1);
        int toIndex = Math.min(index + 9, list.size());

        if (toIndex <= index) {
            return false;
        }

        for (T value : list.subList(index, toIndex)) {
            consumer.accept(value, ++index);
        }

        return true;
    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        entries.sort((c1, c2) -> c2.getValue().compareTo(c1.getValue()));
        return entries;
    }

    public static boolean isPlayer(CommandSourceStack source) {
        return source.getEntity() instanceof Player;
    }
}
