package org.provim.servercore.interfaces;

import net.minecraft.util.math.BlockPos;

public interface EntityWithTarget {

    BlockPos getMovingTarget();

    void setMovingTarget(BlockPos pos);
}
