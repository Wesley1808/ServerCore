package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import org.provim.servercore.interfaces.EntityWithTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PathAwareEntity.class)
public abstract class PathAwareEntityMixin implements EntityWithTarget {
    @Unique
    public BlockPos movingTarget = null;

    @Override
    public BlockPos getMovingTarget() {
        return movingTarget;
    }

    @Override
    public void setMovingTarget(BlockPos pos) {
        this.movingTarget = pos;
    }
}
