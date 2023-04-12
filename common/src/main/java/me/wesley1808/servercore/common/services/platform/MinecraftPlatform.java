package me.wesley1808.servercore.common.services.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public interface MinecraftPlatform {
    Component parseText(String input);

    boolean hasPermission(CommandSourceStack source, String node, int level);
}
