package me.wesley1808.servercore.neoforge.common;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;

public class NeoForgeMinecraftPlatform implements MinecraftPlatform {
    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        if (source.hasPermission(level)) {
            return true;
        }

        ServerPlayer player = source.getPlayer();
        PermissionNode<Boolean> permission = NeoForgePermissions.getPermissionNode(node);
        if (player == null || permission == null) {
            return false;
        }

        return PermissionAPI.getPermission(player, permission);
    }

    @Override
    public Component parseText(MinecraftServer server, String input) {
        String json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(input));
        return ComponentSerialization.CODEC
                .decode(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json))
                .result()
                .map(Pair::getFirst)
                .orElse(Component.empty());
    }
}
