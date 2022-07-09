package me.wesley1808.servercore.mixin.optimizations.mob_spawning;

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
    private static Holder<Biome> fastBiomeLookup(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = ChunkManager.getChunkIfLoaded(level, pos);
        return chunk != null ? ChunkManager.getRoughBiome(chunk, pos) : level.getBiome(pos);
    }
}