package me.wesley1808.servercore.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.wesley1808.servercore.ServerCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.TickingBlockEntity;

import java.util.Map;

/**
 * Utility methods for getting performance related statistics.
 *
 * @author Wesley1808
 */
public final class Statistics {

    public static ImmutableList<Entity> getAllEntities() {
        ImmutableList.Builder<Entity> builder = ImmutableList.builder();
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            builder.addAll(level.getAllEntities());
        }

        return builder.build();
    }

    public static ImmutableList<Entity> getEntitiesNear(ServerPlayer player) {
        ImmutableList.Builder<Entity> builder = ImmutableList.builder();
        for (Entity entity : player.getLevel().getAllEntities()) {
            if (isNearby(player, entity.chunkPosition())) {
                builder.add(entity);
            }
        }

        return builder.build();
    }

    public static ImmutableList<TickingBlockEntity> getAllBlockEntities() {
        ImmutableList.Builder<TickingBlockEntity> builder = ImmutableList.builder();
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            builder.addAll(level.blockEntityTickers);
        }

        return builder.build();
    }

    public static ImmutableList<TickingBlockEntity> getBlockEntitiesNear(ServerPlayer player) {
        ImmutableList.Builder<TickingBlockEntity> builder = ImmutableList.builder();
        for (TickingBlockEntity blockEntity : player.level.blockEntityTickers) {
            if (isNearby(player, new ChunkPos(blockEntity.getPos()))) {
                builder.add(blockEntity);
            }
        }

        return builder.build();
    }

    public static Map<String, Integer> getEntitiesByType(Iterable<Entity> entities) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (Entity entity : entities) {
            ResourceLocation location = EntityType.getKey(entity.getType());
            if (location != null) {
                String key = location.toString();
                map.put(key, map.getOrDefault(key, 0) + 1);
            }
        }

        return map;
    }

    public static Map<String, Integer> getBlockEntitiesByType(Iterable<TickingBlockEntity> blockEntities) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (TickingBlockEntity blockEntity : blockEntities) {
            String key = blockEntity.getType();
            map.put(key, map.getOrDefault(key, 0) + 1);
        }

        return map;
    }

    public static Map<String, Integer> getEntitiesByPlayer(Iterable<ServerPlayer> players) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (ServerPlayer player : players) {
            map.put(player.getScoreboardName(), getEntitiesNear(player).size());
        }

        return map;
    }

    public static Map<String, Integer> getBlockEntitiesByPlayer(Iterable<ServerPlayer> players) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (ServerPlayer player : players) {
            map.put(player.getScoreboardName(), getBlockEntitiesNear(player).size());
        }

        return map;
    }

    public static int getLoadedChunkCount() {
        int count = 0;
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            for (ChunkHolder holder : level.getChunkSource().chunkMap.getChunks()) {
                if (ChunkManager.isChunkLoaded(holder)) {
                    count++;
                }
            }
        }

        return count;
    }

    private static boolean isNearby(Player player, ChunkPos pos) {
        return player.chunkPosition().getChessboardDistance(pos) <= TickManager.getViewDistance();
    }
}
