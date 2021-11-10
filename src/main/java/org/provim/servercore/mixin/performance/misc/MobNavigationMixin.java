package org.provim.servercore.mixin.performance.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GroundPathNavigation.class)
public abstract class MobNavigationMixin extends PathNavigation {
    private MobNavigationMixin(Mob mob, Level level) {
        super(mob, level);
    }

    // Don't load chunks for pathfinding.
    @Inject(method = "createPath(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/pathfinder/Path;", at = @At("HEAD"), cancellable = true)
    private void onlyPathfindIfLoaded(BlockPos target, int distance, CallbackInfoReturnable<Path> cir) {
        if (!ChunkManager.isChunkLoaded(this.level, target)) {
            cir.setReturnValue(null);
        }
    }
}