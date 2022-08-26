package me.wesley1808.servercore.common.config.tables;

import me.wesley1808.servercore.common.config.ConfigEntry;

public class ItemValidationConfig {
    public static final ConfigEntry<Integer> MAX_BOOK_PAGE_SIZE = new ConfigEntry<>(2560, "The max number " +
            "of bytes a single page in a book can contribute to the allowed byte total for a book.");

    public static final ConfigEntry<Double> BOOK_TOTAL_MULTIPLIER = new ConfigEntry<>(0.98D, "Each page has this multiple of bytes from the last page" +
            " as its contribution to the allowed byte total for a book (with the first page being having a multiplier of 1.0)");

    public static final ConfigEntry<Integer> MAX_SIGN_LINE_LENGTH = new ConfigEntry<>(80, "The max number" +
            " of characters a single line in a sign can contain.");
}
