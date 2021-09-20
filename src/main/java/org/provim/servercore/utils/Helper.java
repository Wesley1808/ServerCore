package org.provim.servercore.utils;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Helper {

    private Helper() {
    }

    public static List<ServerPlayerEntity> getNearbyPlayers(ServerWorld world, ChunkPos pos, @Nullable ObjectSet<ServerPlayerEntity> watchingPlayers) {
        final ChunkTicketManager manager = ChunkManager.getTicketManager(world);
        if (manager == null || !manager.method_20800(pos.toLong())) {
            return List.of();
        }

        final Predicate<ServerPlayerEntity> predicate = player -> !player.isSpectator() && getSquaredDistance(pos, player) < 16384.0D;
        return watchingPlayers != null ? getNearbyWatchingPlayers(watchingPlayers, predicate) : world.getPlayers(predicate);
    }

    private static List<ServerPlayerEntity> getNearbyWatchingPlayers(ObjectSet<ServerPlayerEntity> watchingPlayers, Predicate<ServerPlayerEntity> predicate) {
        final List<ServerPlayerEntity> players = new ArrayList<>();
        for (ServerPlayerEntity player : watchingPlayers) {
            if (predicate.test(player)) {
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
