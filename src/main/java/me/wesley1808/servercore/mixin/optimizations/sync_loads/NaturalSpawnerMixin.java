package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Redirect(
            method = "isRightDistanceToPlayerAndSpawnPoint",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isNaturalSpawningAllowed(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean servercore$onlySpawnIfLoaded(ServerLevel level, BlockPos pos) {
        return level.isNaturalSpawningAllowed(pos) && ChunkManager.isChunkLoaded(level, pos);
    }
}