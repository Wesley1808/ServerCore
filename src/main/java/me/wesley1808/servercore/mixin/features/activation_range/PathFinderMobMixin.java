package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.interfaces.activation_range.IPathFinderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * Patch Author: Aikar (aikar@aikar.co)
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
