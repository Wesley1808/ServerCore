package me.wesley1808.servercore.common.interfaces.activation_range;

import net.minecraft.core.BlockPos;

public interface IPathFinderMob {
    BlockPos getMovingTarget();

    void setMovingTarget(BlockPos pos);
}
