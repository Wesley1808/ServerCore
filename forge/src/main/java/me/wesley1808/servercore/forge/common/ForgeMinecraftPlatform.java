package me.wesley1808.servercore.forge.common;

import me.wesley1808.servercore.common.services.platform.MinecraftPlatform;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.Nullable;

public class ForgeMinecraftPlatform implements MinecraftPlatform {
    @Override
    public boolean hasPermission(CommandSourceStack source, String node, int level) {
        if (source.hasPermission(level)) {
            return true;
        }

        ServerPlayer player = source.getPlayer();
        PermissionNode<Boolean> permission = ForgePermissions.getPermissionNode(node);
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
        return ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(key));
    }
}
