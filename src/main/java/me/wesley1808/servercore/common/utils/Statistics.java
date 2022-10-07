package me.wesley1808.servercore.common.utils;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import net.minecraft.server.MinecraftServer;
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
    private final MinecraftServer server;

    public Statistics(MinecraftServer server) {
        this.server = server;
    }

    public static Statistics getInstance(MinecraftServer server) {
        return ((IMinecraftServer) server).getStatistics();
    }

    public List<Entity> getAllEntities() {
        return this.getAll(ServerLevel::getAllEntities);
    }

    public List<TickingBlockEntity> getAllBlockEntities() {
        return this.getAll(level -> level.blockEntityTickers);
    }

    public Map<String, Integer> getEntitiesByType(Iterable<Entity> entities) {
        return this.getByType(entities, (entity) -> EntityType.getKey(entity.getType()).toString());
    }

    public Map<String, Integer> getBlockEntitiesByType(Iterable<TickingBlockEntity> blockEntities) {
        return this.getByType(blockEntities, TickingBlockEntity::getType);
    }

    public Map<String, Integer> getEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return this.getByPlayer(players, this::getEntitiesNear);
    }

    public Map<String, Integer> getBlockEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return this.getByPlayer(players, this::getBlockEntitiesNear);
    }

    private <T> List<T> getAll(Function<ServerLevel, Iterable<T>> function) {
        List<T> list = new ObjectArrayList<>();
        for (ServerLevel level : this.server.getAllLevels()) {
            Iterables.addAll(list, function.apply(level));
        }

        return list;
    }

    private <T> Map<String, Integer> getByType(Iterable<T> iterable, Function<T, String> function) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (T value : iterable) {
            map.addTo(function.apply(value), 1);
        }

        return map;
    }

    private Map<String, Integer> getByPlayer(Iterable<ServerPlayer> players, Function<ServerPlayer, List<?>> function) {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
        for (ServerPlayer player : players) {
            map.put(player.getScoreboardName(), function.apply(player).size());
        }

        return map;
    }

    public List<Entity> getEntitiesNear(ServerPlayer player) {
        List<Entity> list = new ObjectArrayList<>();
        for (Entity entity : player.getLevel().getAllEntities()) {
            if (this.isNearby(player, entity.chunkPosition())) {
                list.add(entity);
            }
        }

        return list;
    }

    public List<TickingBlockEntity> getBlockEntitiesNear(ServerPlayer player) {
        List<TickingBlockEntity> list = new ObjectArrayList<>();
        for (TickingBlockEntity blockEntity : player.level.blockEntityTickers) {
            if (this.isNearby(player, new ChunkPos(blockEntity.getPos()))) {
                list.add(blockEntity);
            }
        }

        return list;
    }

    public int getChunkCount(boolean onlyLoaded) {
        int count = 0;
        for (ServerLevel level : this.server.getAllLevels()) {
            if (onlyLoaded) {
                for (ChunkHolder holder : level.getChunkSource().chunkMap.visibleChunkMap.values()) {
                    if (ChunkManager.hasChunk(holder)) {
                        count++;
                    }
                }
            } else {
                count += level.getChunkSource().getLoadedChunksCount();
            }
        }

        return count;
    }


    private boolean isNearby(Player player, ChunkPos pos) {
        return player.chunkPosition().getChessboardDistance(pos) <= DynamicSetting.VIEW_DISTANCE.get();
    }
}
