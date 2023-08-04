package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureCheck.class)
public class StructureCheckMixin {
    @Shadow
    @Final
    private ChunkGenerator chunkGenerator;

    @Shadow
    @Final
    private RandomState randomState;

    @Inject(
            method = "checkStart",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/StructureCheck;tryLoadFromStorage(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/Structure;ZJ)Lnet/minecraft/world/level/levelgen/structure/StructureCheckResult;",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$skipInvalidBiomes(ChunkPos chunkPos, Structure structure, boolean skipKnownStructures, CallbackInfoReturnable<StructureCheckResult> cir) {
        // Quick checks to validate the biome for certain structures without performing expensive noise calculations.
        // This is mainly done to significantly speed up locating buried treasures.
        BlockPos pos = this.servercore$getRoughStructurePosition(structure, chunkPos);
        if (pos != null && !this.servercore$isBiomeValid(structure, pos)) {
            cir.setReturnValue(StructureCheckResult.START_NOT_PRESENT);
        }
    }

    @Unique
    private boolean servercore$isBiomeValid(Structure structure, BlockPos pos) {
        return structure.biomes().contains(this.chunkGenerator.getBiomeSource().getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2, this.randomState.sampler()));
    }

    @Unique
    private BlockPos servercore$getRoughStructurePosition(Structure structure, ChunkPos chunkPos) {
        // The height isn't always guaranteed to be correct, but it's only used for biome checks.
        // There might be an extremely small chance that it will return the wrong biome, but it's arguably better than watchdog crashing the server.
        if (structure instanceof BuriedTreasureStructure) {
            return chunkPos.getMiddleBlockPosition(this.chunkGenerator.getSeaLevel());
        }

        return null;
    }
}
