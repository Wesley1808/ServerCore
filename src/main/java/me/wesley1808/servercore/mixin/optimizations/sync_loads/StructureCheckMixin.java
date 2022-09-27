package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(StructureCheck.class)
public class StructureCheckMixin {
    @Shadow
    @Final
    private ChunkGenerator chunkGenerator;

    @Shadow
    @Final
    private RandomState randomState;

    /**
     * Always check for biomes before loading chunks to find structures.
     * This way we can skip all chunk loads inside biomes that cannot generate the given structure.
     */
    @Redirect(
            method = "canCreateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"
            )
    )
    private Optional<Structure.GenerationStub> servercore$skipInvalidBiomes(Structure structure, Structure.GenerationContext context, ChunkPos chunkPos, Structure structure2) {
        // Quick checks to find the structure position inside the chunk without performing expensive noise calculations.
        BlockPos pos = this.getStructurePosition(structure, chunkPos);
        if (pos != null && this.cannotGenerateAt(structure, pos)) {
            return Optional.empty();
        }

        // Locates the (accurate) structure position inside the chunk.
        Optional<Structure.GenerationStub> optional = structure.findGenerationPoint(context);
        if (optional.isEmpty() || this.cannotGenerateAt(structure, optional.get().position())) {
            return Optional.empty();
        }

        return optional;
    }

    private boolean cannotGenerateAt(Structure structure, BlockPos pos) {
        int x = QuartPos.fromBlock(pos.getX());
        int y = QuartPos.fromBlock(pos.getY());
        int z = QuartPos.fromBlock(pos.getZ());

        return !structure.biomes().contains(this.chunkGenerator.getBiomeSource().getNoiseBiome(x, y, z, this.randomState.sampler()));
    }

    private BlockPos getStructurePosition(Structure structure, ChunkPos pos) {
        if (structure instanceof BuriedTreasureStructure) {
            return pos.getMiddleBlockPosition(this.chunkGenerator.getSeaLevel());
        }

        return null;
    }
}
