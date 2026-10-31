package me.wesley1808.servercore.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for getting chunks.
 *
 * @author Wesley1808
 */
public class ChunkManager {

    @Nullable
    public static LevelChunk getChunkNow(LevelAccessor level, int chunkX, int chunkZ) {
        return level.getChunkSource().getChunkNow(chunkX, chunkZ);
    }

    @Nullable
    public static LevelChunk getChunkNow(LevelAccessor level, BlockPos pos) {
        return getChunkNow(level,
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ())
        );
    }

    @Nullable
    public static ChunkAccess getChunkNow(LevelReader levelReader, int chunkX, int chunkZ) {
        if (levelReader instanceof LevelAccessor level) {
            return getChunkNow(level, chunkX, chunkZ);
        } else {
            return levelReader.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        }
    }

    public static boolean hasChunk(LevelAccessor level, BlockPos pos) {
        return getChunkNow(level, pos) != null;
    }

    public static boolean hasChunk(LevelAccessor level, int chunkX, int chunkZ) {
        return getChunkNow(level, chunkX, chunkZ) != null;
    }

    @NotNull
    public static Holder<Biome> getRoughBiome(LevelAccessor level, BlockPos pos) {
        ChunkAccess chunk = getChunkNow(level, pos);
        int quartX = QuartPos.fromBlock(pos.getX());
        int quartY = QuartPos.fromBlock(pos.getY());
        int quartZ = QuartPos.fromBlock(pos.getZ());
        return chunk != null ? chunk.getNoiseBiome(quartX, quartY, quartZ) : level.getUncachedNoiseBiome(quartX, quartY, quartZ);
    }

    public static boolean areChunksLoadedForMove(LevelAccessor level, Entity entity, double toX, double toZ) {
        double dx = toX - entity.getX();
        double dz = toZ - entity.getZ();
        return ChunkManager.areChunksLoadedForMove(level, entity.getBoundingBox().expandTowards(dx, 0, dz));
    }

    // Utility method from PaperMC (MC-Utils.patch)
    public static boolean areChunksLoadedForMove(LevelAccessor level, AABB box) {
        int minBlockX = Mth.floor(box.minX - 1.0E-7D) - 3;
        int maxBlockX = Mth.floor(box.maxX + 1.0E-7D) + 3;

        int minBlockZ = Mth.floor(box.minZ - 1.0E-7D) - 3;
        int maxBlockZ = Mth.floor(box.maxZ + 1.0E-7D) + 3;

        int minChunkX = SectionPos.blockToSectionCoord(minBlockX);
        int maxChunkX = SectionPos.blockToSectionCoord(maxBlockX);

        int minChunkZ = SectionPos.blockToSectionCoord(minBlockZ);
        int maxChunkZ = SectionPos.blockToSectionCoord(maxBlockZ);

        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                if (!hasChunk(level, chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        return true;
    }
}