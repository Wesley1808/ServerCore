package me.wesley1808.servercore.mixin.features.spawn_chunks;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Redirect(
            method = "setDefaultSpawnPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    private <T> void servercore$preventAddingRegionTicket(ServerChunkCache chunkCache, TicketType<T> ticketType, ChunkPos chunkPos, int i, T object) {
        if (!FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            chunkCache.addRegionTicket(ticketType, chunkPos, i, object);
        }
    }
}
