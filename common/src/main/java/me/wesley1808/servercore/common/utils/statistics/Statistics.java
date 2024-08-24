package me.wesley1808.servercore.common.utils.statistics;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.utils.ChunkManager;
import me.wesley1808.servercore.common.utils.statistics.entry.EntityStatisticEntry;
import me.wesley1808.servercore.common.utils.statistics.entry.StatisticEntry;
import net.minecraft.core.BlockPos;
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
import java.util.function.Supplier;

/**
 * Utility methods for getting performance related statistics.
 *
 * @author Wesley1808
 */
public class Statistics {
    private final MinecraftServer server;

    public Statistics(MinecraftServer server) {
        this.server = server;
    }

    public static Statistics getInstance(MinecraftServer server) {
        return ((IMinecraftServer) server).servercore$getStatistics();
    }

    public List<Entity> getAllEntities() {
        return this.getAll(ServerLevel::getAllEntities);
    }

    public List<TickingBlockEntity> getAllBlockEntities() {
        return this.getAll(level -> level.blockEntityTickers);
    }

    public Map<String, StatisticEntry<Entity>> getEntitiesByType(Iterable<Entity> entities) {
        return this.getByType(entities,
                (entity) -> EntityType.getKey(entity.getType()).toString(),
                EntityStatisticEntry::new
        );
    }

    public Map<String, StatisticEntry<TickingBlockEntity>> getBlockEntitiesByType(Iterable<TickingBlockEntity> blockEntities) {
        return this.getByType(blockEntities, TickingBlockEntity::getType, StatisticEntry::new);
    }

    public Map<String, StatisticEntry<Entity>> getEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return this.getByPlayer(players, this::getEntitiesNear, EntityStatisticEntry::new);
    }

    public Map<String, StatisticEntry<TickingBlockEntity>> getBlockEntitiesByPlayer(Iterable<ServerPlayer> players) {
        return this.getByPlayer(players, this::getBlockEntitiesNear, StatisticEntry::new);
    }

    private <T> List<T> getAll(Function<ServerLevel, Iterable<T>> function) {
        List<T> list = new ObjectArrayList<>();
        for (ServerLevel level : this.server.getAllLevels()) {
            Iterables.addAll(list, function.apply(level));
        }

        return list;
    }

    private <T> Map<String, StatisticEntry<T>> getByType(Iterable<T> iterable, Function<T, String> function, Supplier<StatisticEntry<T>> supplier) {
        Map<String, StatisticEntry<T>> map = new Object2ObjectOpenHashMap<>();
        for (T value : iterable) {
            String key = function.apply(value);
            StatisticEntry<T> entry = map.computeIfAbsent(key, k -> supplier.get());
            entry.increment(value);
        }

        return map;
    }

    private <T> Map<String, StatisticEntry<T>> getByPlayer(Iterable<ServerPlayer> players, Function<ServerPlayer, List<T>> function, Supplier<StatisticEntry<T>> supplier) {
        Map<String, StatisticEntry<T>> map = new Object2ObjectOpenHashMap<>();
        for (ServerPlayer player : players) {
            StatisticEntry<T> entry = supplier.get();
            for (T value : function.apply(player)) {
                entry.increment(value);
            }
            map.put(player.getScoreboardName(), entry);
        }

        return map;
    }

    public List<Entity> getEntitiesNear(ServerPlayer player) {
        List<Entity> list = new ObjectArrayList<>();
        for (Entity entity : player.serverLevel().getAllEntities()) {
            if (this.isNearby(player, entity.chunkPosition())) {
                list.add(entity);
            }
        }

        return list;
    }

    public List<TickingBlockEntity> getBlockEntitiesNear(ServerPlayer player) {
        List<TickingBlockEntity> list = new ObjectArrayList<>();
        for (TickingBlockEntity blockEntity : player.level().blockEntityTickers) {
            BlockPos pos = blockEntity.getPos();
            // noinspection ConstantConditions - Lithium's sleeping block entities can have a null position.
            if (pos != null && this.isNearby(player, new ChunkPos(pos))) {
                list.add(blockEntity);
            }
        }

        return list;
    }

    public int getChunkCount(boolean onlyLoaded) {
        int count = 0;
        for (ServerLevel level : this.server.getAllLevels()) {
            if (onlyLoaded) {
                for (ChunkHolder holder : level.getChunkSource().chunkMap.getChunks()) {
                    if (ChunkManager.hasChunk(level, holder)) {
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
