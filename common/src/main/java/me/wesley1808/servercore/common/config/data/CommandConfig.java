package me.wesley1808.servercore.common.config.data;

import me.wesley1808.servercore.common.config.serialization.TextColorSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

@ConfSerialisers(TextColorSerializer.class)
public interface CommandConfig {
    @Order(1)
    @ConfKey("status-enabled")
    @DefaultBoolean(true)
    @ConfComments("Enables the /servercore status command.")
    boolean statusCommandEnabled();

    @Order(2)
    @ConfKey("mobcaps-enabled")
    @DefaultBoolean(true)
    @ConfComments("Enables the /mobcaps command.")
    boolean mobcapsCommandEnabled();

    @Order(3)
    @ConfKey("colors.primary")
    @DefaultObject("defaultPrimaryColor")
    @ConfComments({
            "The colors used in command feedback. You can use hex codes or minecraft legacy color names.",
            "The primary color is the most used color in command feedback."
    })
    TextColor primaryColor();

    @Order(4)
    @ConfKey("colors.secondary")
    @DefaultObject("defaultSecondaryColor")
    @ConfComments("The secondary color is used for highlighting important information, like values.")
    TextColor secondaryColor();

    @Order(5)
    @ConfKey("colors.tertiary")
    @DefaultObject("defaultTertiaryColor")
    @ConfComments("The tertiary color is mostly used for text in titles.")
    TextColor tertiaryColor();

    default String primaryHex() {
        return this.primaryColor().serialize();
    }

    default String secondaryHex() {
        return this.secondaryColor().serialize();
    }

    default String tertiaryHex() {
        return this.tertiaryColor().serialize();
    }

    default int primaryValue() {
        return this.primaryColor().getValue();
    }

    default int secondaryValue() {
        return this.secondaryColor().getValue();
    }

    default int tertiaryValue() {
        return this.tertiaryColor().getValue();
    }

    static TextColor defaultPrimaryColor() {
        return TextColor.fromLegacyFormat(ChatFormatting.DARK_AQUA);
    }

    static TextColor defaultSecondaryColor() {
        return TextColor.fromLegacyFormat(ChatFormatting.GREEN);
    }

    static TextColor defaultTertiaryColor() {
        return TextColor.fromLegacyFormat(ChatFormatting.AQUA);
    }
}