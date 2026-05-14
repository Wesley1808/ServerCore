package me.wesley1808.servercore.fabric.common;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.wesley1808.servercore.common.ServerCore;
import me.wesley1808.servercore.common.services.PermNode;
import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public class FabricMinecraftPlatform implements MinecraftPlatform {
    private final NodeParser parser = NodeParser.builder()
            .simplifiedTextFormat()
            .quickText()
            .build();

    @Override
    public boolean hasPermission(CommandSourceStack source, PermNode node) {
        Identifier permission = Identifier.fromNamespaceAndPath(ServerCore.MODID, node.id());
        return source.checkPermission(permission).orElseGet(() -> node.defaultResolver().test(source.permissions()));
    }

    @Override
    public Component parseText(MinecraftServer server, String input) {
        return this.parser.parseComponent(input, ParserContext.of());
    }
}
