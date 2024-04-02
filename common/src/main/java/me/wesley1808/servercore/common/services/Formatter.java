package me.wesley1808.servercore.common.services;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import me.wesley1808.servercore.common.services.platform.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Formatter {

    public static Component parse(String input) {
        CommandConfig config = Config.get().commands();
        return PlatformHelper.parseText(input
                .replace("#primary", config.primaryHex())
                .replace("#secondary", config.secondaryHex())
                .replace("#tertiary", config.tertiaryHex())
        );
    }

    public static void addLines(MutableComponent out, int lineLength, int lineColor, Component component) {
        Component line = Component.literal(" ".repeat(lineLength))
                .withStyle(ChatFormatting.STRIKETHROUGH)
                .withStyle(style -> style.withColor(lineColor));

        out.append(line);
        out.append(" ");
        out.append(component);
        out.append(" ");
        out.append(line);
    }
}
