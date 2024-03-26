package me.wesley1808.servercore.common.services.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public interface MinecraftPlatform {
    boolean hasPermission(CommandSourceStack source, String node, int level);

    boolean shouldForceChunkTicks(ChunkMap chunkMap, ChunkPos pos);

    Component parseText(String input);

    @Nullable
    EntityType<?> getEntityType(String key);
}
