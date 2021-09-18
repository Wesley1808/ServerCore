package org.provim.servercore.utils;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.List;

public final class Helper {

    private Helper() {
    }

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerWorld world, ChunkPos pos) {
        final ChunkTicketManager manager = ChunkManager.getTicketManager(world);
        return manager == null || !manager.method_20800(pos.toLong()) ? List.of() : world.getPlayers(player -> !player.isSpectator() && getSquaredDistance(pos, player) < 16384.0D);
    }

    public static double getSquaredDistance(ChunkPos pos, Entity entity) {
        final double x = ChunkSectionPos.getOffsetPos(pos.x, 8) - entity.getX();
        final double z = ChunkSectionPos.getOffsetPos(pos.z, 8) - entity.getZ();
        return x * x + z * z;
    }
}
