package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.config.data.mob_spawning.EnforcedMobcap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import java.util.List;

public class Mobcaps {
    public static final LocalMobCapCalculator.MobCounts EMPTY_MOBCOUNTS = new LocalMobCapCalculator.MobCounts();
    public static final int MAGIC_NUMBER = (int) Math.pow(17.0, 2.0);

    public static boolean canSpawnForCategory(Level level, ChunkPos pos, MobCategory category, EnforcedMobcap config) {
        return !(level instanceof ServerLevel serverLevel) || Mobcaps.canSpawnForCategory(serverLevel, pos, category, config);
    }

    public static boolean canSpawnForCategory(ServerLevel level, ChunkPos pos, MobCategory category, EnforcedMobcap config) {
        NaturalSpawner.SpawnState state = level.getChunkSource().getLastSpawnState();
        if (state == null || category == MobCategory.MISC || !config.enforcesMobcap()) {
            return true;
        }

        final int capacity = category.getMaxInstancesPerChunk() + config.additionalCapacity();
        final int globalCapacity = Mobcaps.toGlobalCapacity(state, capacity);

        final int globalCount = state.getMobCategoryCounts().getInt(category);
        if (globalCount >= globalCapacity) {
            return false;
        }

        for (ServerPlayer player : Mobcaps.getPlayersNear(level, pos, state.localMobCapCalculator)) {
            var mobCounts = state.localMobCapCalculator.playerMobCounts.get(player);
            if (mobCounts == null || mobCounts.counts.getOrDefault(category, 0) < capacity) {
                return true;
            }
        }

        return false;
    }

    private static int toGlobalCapacity(NaturalSpawner.SpawnState state, int capacity) {
        return capacity * state.getSpawnableChunkCount() / MAGIC_NUMBER;
    }

    private static List<ServerPlayer> getPlayersNear(ServerLevel level, ChunkPos pos, LocalMobCapCalculator calculator) {
        return Environment.MOD_VMP
                ? level.getChunkSource().chunkMap.getPlayersCloseForSpawning(pos)
                : calculator.getPlayersNear(pos);
    }
}
