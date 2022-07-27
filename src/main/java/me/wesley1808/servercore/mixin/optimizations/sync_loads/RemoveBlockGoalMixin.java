package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RemoveBlockGoal.class)
public abstract class RemoveBlockGoalMixin {

    @Redirect(method = "isValidTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"))
    private ChunkAccess servercore$onlyValidateIfLoaded(LevelReader levelReader, int x, int z, ChunkStatus status, boolean create) {
        if (levelReader instanceof Level level) {
            return ChunkManager.getChunkIfLoaded(level, x, z);
        }

        return levelReader.getChunk(x, z, status, create);
    }
}
