package org.provim.servercore.mixin.performance;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobNavigation.class)
public abstract class MobNavigationMixin extends EntityNavigation {
    protected MobNavigationMixin(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    // Don't load chunks for pathfinding.
    @Inject(method = "findPathTo(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("HEAD"), cancellable = true)
    private void onlyPathfindIfLoaded(BlockPos target, int distance, CallbackInfoReturnable<Path> cir) {
        if (!ChunkManager.isChunkLoaded(this.world, target)) {
            cir.setReturnValue(null);
        }
    }
}