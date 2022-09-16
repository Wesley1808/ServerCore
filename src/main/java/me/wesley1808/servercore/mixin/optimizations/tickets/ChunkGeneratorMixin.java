package me.wesley1808.servercore.mixin.optimizations.tickets;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Redirect(
            method = "getMobsAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/StructureManager;getAllStructuresAt(Lnet/minecraft/core/BlockPos;)Ljava/util/Map;"
            )
    )
    private Map<Structure, LongSet> servercore$preventAddingTickets(StructureManager structureManager, BlockPos pos) {
        if (structureManager.level instanceof ServerLevel level) {
            LevelChunk chunk = ChunkManager.getChunkIfLoaded(level, pos);
            if (chunk != null) {
                return chunk.getAllReferences();
            }
        }

        return structureManager.getAllStructuresAt(pos);
    }
}
