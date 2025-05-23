package me.wesley1808.servercore.fabric.common;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class FabricMinecraftPlatform implements MinecraftPlatform {
    private final NodeParser parser = NodeParser.builder()
            .simplifiedTextFormat()
            .quickText()
            .build();

    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        String permission = String.format("%s.%s", ServerCore.MODID, node);
        return Permissions.check(source, permission, level);
    }

    @Override
    public Component parseText(MinecraftServer server, String input) {
        return this.parser.parseText(input, ParserContext.of());
    }
}
