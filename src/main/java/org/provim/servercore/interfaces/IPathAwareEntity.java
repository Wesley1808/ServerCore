package org.provim.servercore.interfaces;

import net.minecraft.util.math.BlockPos;

public interface IPathAwareEntity {

    BlockPos getMovingTarget();

    void setMovingTarget(BlockPos pos);
}
