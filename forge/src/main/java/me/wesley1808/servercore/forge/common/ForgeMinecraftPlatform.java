package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

public class ForgeMinecraftPlatform implements MinecraftPlatform {
    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        if (source.hasPermission(level)) {
            return true;
        }

        Entity entity = source.getEntity();
        PermissionNode<Boolean> permission = ForgePermissions.getPermissionNode(node);
        if (permission == null || !(entity instanceof ServerPlayer player)) {
            return false;
        }

        return PermissionAPI.getPermission(player, permission);
    }

    @Override
    public Component parseText(String input) {
        return Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(input)));
    }
}
