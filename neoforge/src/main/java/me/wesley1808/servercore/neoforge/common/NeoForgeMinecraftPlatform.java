package me.wesley1808.servercore.neoforge.common;

import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.Nullable;

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
    public boolean shouldForceChunkTicks(ChunkMap chunkMap, ChunkPos pos) {
        return chunkMap.getDistanceManager().shouldForceTicks(pos.toLong());
    }

    @Override
    public Component parseText(String input) {
        return Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(input)));
    }

    @Override
    @Nullable
    public EntityType<?> getEntityType(String key) {
        return null;
    }
}
