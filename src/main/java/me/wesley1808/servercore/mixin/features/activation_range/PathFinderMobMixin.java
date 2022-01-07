package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.interfaces.activation_range.IPathFinderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PathfinderMob.class)
public abstract class PathFinderMobMixin implements IPathFinderMob {
    @Unique
    public BlockPos movingTarget = null;

    @Override
    public BlockPos getMovingTarget() {
        return this.movingTarget;
    }

    @Override
    public void setMovingTarget(BlockPos pos) {
        this.movingTarget = pos;
    }
}
