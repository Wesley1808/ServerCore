package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import org.provim.servercore.interfaces.EntityWithTarget;
import org.provim.servercore.interfaces.TargetPosition;
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

@Mixin(MoveToTargetPosGoal.class)
public abstract class MoveToTargetPosGoalMixin extends Goal implements TargetPosition {
    @Shadow
    protected BlockPos targetPos;

    @Shadow
    @Final
    protected PathAwareEntity mob;

    @Inject(method = "findTargetPos", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/goal/MoveToTargetPosGoal;targetPos:Lnet/minecraft/util/math/BlockPos;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setTargetPos(CallbackInfoReturnable<Boolean> cir, int var1, int var2, BlockPos var3, BlockPos.Mutable mutable, int var5, int var6, int var7, int var8) {
        ((TargetPosition) this).setTargetPosition(mutable.mutableCopy());
    }

    @Override
    public void setTargetPosition(BlockPos pos) {
        this.targetPos = pos;
        ((EntityWithTarget) this.mob).setMovingTarget(pos != BlockPos.ORIGIN ? pos : null);
    }
}
