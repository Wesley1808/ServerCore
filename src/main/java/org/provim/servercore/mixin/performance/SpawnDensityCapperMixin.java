package org.provim.servercore.mixin.performance;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnDensityCapper;
import org.provim.servercore.mixin.accessor.TACSAccessor;
import org.provim.servercore.utils.Helper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SpawnDensityCapper.class)
public abstract class SpawnDensityCapperMixin {

    @Shadow
    @Final
    private ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    private Long2ObjectMap<List<ServerPlayerEntity>> chunkPosToMobSpawnablePlayers;

    // Avoid stream code.
    @Inject(method = "getMobSpawnablePlayers", at = @At("HEAD"), cancellable = true)
    private void getNearbyPlayers(ChunkPos pos, CallbackInfoReturnable<List<ServerPlayerEntity>> cir) {
        cir.setReturnValue(this.chunkPosToMobSpawnablePlayers.computeIfAbsent(pos.toLong(), l -> Helper.getNearbyPlayers(((TACSAccessor) this.threadedAnvilChunkStorage).getWorld(), pos)));
    }
}
