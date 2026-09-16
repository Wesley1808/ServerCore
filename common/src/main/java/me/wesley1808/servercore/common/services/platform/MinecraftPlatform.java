package me.wesley1808.servercore.common.services.platform;

import me.wesley1808.servercore.common.services.PermNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public interface MinecraftPlatform {
    boolean hasPermission(CommandSourceStack source, PermNode node);

    Component parseText(MinecraftServer server, String input);
}
