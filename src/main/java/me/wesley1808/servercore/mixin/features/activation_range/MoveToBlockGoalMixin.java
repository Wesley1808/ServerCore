package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.interfaces.activation_range.IPathFinderMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(MoveToBlockGoal.class)
public abstract class MoveToBlockGoalMixin extends Goal {
    @Shadow
    protected BlockPos blockPos;

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(
            method = "findNearestBlock",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "FIELD",
                    ordinal = 0,
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/entity/ai/goal/MoveToBlockGoal;blockPos:Lnet/minecraft/core/BlockPos;"
            )
    )
    public void setTargetPos(CallbackInfoReturnable<Boolean> cir, int i, int j, BlockPos blockPos, BlockPos.MutableBlockPos mutable, int k, int l, int m, int n) {
        this.setTargetPosition(mutable.immutable());
    }

    private void setTargetPosition(BlockPos pos) {
        this.blockPos = pos;
        ((IPathFinderMob) this.mob).setMovingTarget(pos != BlockPos.ZERO ? pos : null);
    }

    @Override
    public void stop() {
        super.stop();
        this.setTargetPosition(BlockPos.ZERO);
    }
}
