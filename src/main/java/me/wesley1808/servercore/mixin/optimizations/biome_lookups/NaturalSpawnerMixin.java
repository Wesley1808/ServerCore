package me.wesley1808.servercore.mixin.optimizations.biome_lookups;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Redirect(
            method = {"mobsAt", "getRandomSpawnMobAt"},
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
            )
    )
    private static Holder<Biome> servercore$fastBiomeLookup(ServerLevel level, BlockPos pos) {
        final LevelChunk chunk = ChunkManager.getChunkIfLoaded(level, pos);
        final int x = pos.getX() >> 2;
        final int y = pos.getY() >> 2;
        final int z = pos.getZ() >> 2;

        return chunk != null ? chunk.getNoiseBiome(x, y, z) : level.getUncachedNoiseBiome(x, y, z);
    }
}