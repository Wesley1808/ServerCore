package org.provim.servercore.utils;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.provim.servercore.mixin.accessor.TACSAccessor;

import java.util.ArrayList;
import java.util.List;

public final class Helper {
    private Helper() {
    }

    // Avoid stream allocations.
    public static boolean isTooFarFromPlayerToSpawnMobs(TACSAccessor chunkStorage, ChunkPos pos) {
        return Helper.getNearbyWatchingPlayers(chunkStorage, pos).isEmpty();
    }

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerWorld world, ChunkPos pos) {
        final ChunkTicketManager manager = ChunkManager.getTicketManager(world);
        if (manager == null || !manager.method_20800(pos.toLong())) {
            return List.of();
        }

        return world.getPlayers(player -> !player.isSpectator() && getSquaredDistance(pos, player) < 16384.0D);
    }

    public static List<ServerPlayerEntity> getNearbyWatchingPlayers(TACSAccessor chunkStorage, ChunkPos pos) {
        final ChunkTicketManager manager = ChunkManager.getTicketManager(chunkStorage.getWorld());
        if (manager == null || !manager.method_20800(pos.toLong())) {
            return List.of();
        }

        final List<ServerPlayerEntity> players = new ArrayList<>();
        for (final ServerPlayerEntity player : chunkStorage.getPlayerChunkWatchingManager().watchingPlayers.keySet()) {
            if (!player.isSpectator() && getSquaredDistance(pos, player) < 16384.0D) {
                players.add(player);
            }
        }

        return players;
    }

    public static double getSquaredDistance(ChunkPos pos, Entity entity) {
        final double x = ChunkSectionPos.getOffsetPos(pos.x, 8) - entity.getX();
        final double z = ChunkSectionPos.getOffsetPos(pos.z, 8) - entity.getZ();
        return x * x + z * z;
    }
}
