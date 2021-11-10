package org.provim.servercore.interfaces;

import net.minecraft.core.BlockPos;

public interface IPathFinderMob {

    BlockPos getMovingTarget();

    void setMovingTarget(BlockPos pos);
}
