package me.wesley1808.servercore.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.wesley1808.servercore.ServerCore;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.TickingBlockEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility methods for getting performance related statistics.
 *
 * @author Wesley1808
 */
public final class Statistics {

    public static ImmutableList<Entity> getAllEntities() {
        return Statistics.getAll(ServerLevel::getAllEntities);
    }

    public static ImmutableList<TickingBlockEntity> getAllBlockEntities() {
        return Statistics.getAll(level -> level.blockEntityTickers);
    }

    public static Map<String, Integer> getEntitiesByType(Iterable<Entity> entities) {
        return Statistics.getByType(entities, (entity) -> EntityType.getKey(entity.getType()).toString());
    }

    public static Map<String, Integer> getBlockEntitiesByType(Iterable<TickingBlockEntity> blockEntities) {
        return Statistics.getByType(blockEntities, TickingBlockEntity::getType);
    }

    public static Map<String, Integer> getEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return Statistics.getByPlayer(players, Statistics::getEntitiesNear);
    }

    public static Map<String, Integer> getBlockEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return Statistics.getByPlayer(players, Statistics::getBlockEntitiesNear);
    }

    private static <T> ImmutableList<T> getAll(Function<ServerLevel, Iterable<T>> function) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            builder.addAll(function.apply(level));
        }

        return builder.build();
    }

    private static <T> Map<String, Integer> getByType(Iterable<T> iterable, Function<T, String> function) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (T value : iterable) {
            map.addTo(function.apply(value), 1);
        }

        return map;
    }

    private static Map<String, Integer> getByPlayer(Iterable<ServerPlayer> players, Function<ServerPlayer, List<?>> function) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (ServerPlayer player : players) {
            map.put(player.getScoreboardName(), function.apply(player).size());
        }

        return map;
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

    public static ImmutableList<TickingBlockEntity> getBlockEntitiesNear(ServerPlayer player) {
        ImmutableList.Builder<TickingBlockEntity> builder = ImmutableList.builder();
        for (TickingBlockEntity blockEntity : player.level.blockEntityTickers) {
            if (isNearby(player, new ChunkPos(blockEntity.getPos()))) {
                builder.add(blockEntity);
            }
        }

        return builder.build();
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
