package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.dynamic.DynamicSetting;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class ChunkTickManager {
    public static final int ENTITY_TICKING_LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);

    public static boolean isChunkTickable(ChunkPos pos, ServerPlayer player) {
        return isWithinChunkDistance(player.chunkPosition().getChessboardDistance(pos));
    }

    public static boolean isChunkTickable(byte chunkLevel, int maxDistance) {
        return ChunkLevel.isEntityTicking(chunkLevel) && isWithinChunkDistance(distanceFromSource(chunkLevel, maxDistance));
    }

    private static int distanceFromSource(byte chunkLevel, int maxDistance) {
        return Math.abs(ENTITY_TICKING_LEVEL - chunkLevel - Math.min(maxDistance, ENTITY_TICKING_LEVEL));
    }

    private static boolean isWithinChunkDistance(int chunkDistance) {
        return chunkDistance <= DynamicSetting.CHUNK_TICK_DISTANCE.get();
    }
}
