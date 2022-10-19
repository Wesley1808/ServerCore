package me.wesley1808.servercore.mixin.features.spawn_chunks;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @WrapWithCondition(
            method = "setDefaultSpawnPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    private boolean servercore$preventAddingRegionTicket(ServerChunkCache chunkCache, TicketType<?> ticketType, ChunkPos chunkPos, int i, Object object) {
        return !FeatureConfig.DISABLE_SPAWN_CHUNKS.get();
    }
}
