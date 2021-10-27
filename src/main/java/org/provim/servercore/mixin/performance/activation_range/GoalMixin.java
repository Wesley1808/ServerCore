package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.ai.goal.Goal;
import org.provim.servercore.utils.activation_range.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Goal.class)
public class GoalMixin {

    @Inject(method = "stop", at = @At("HEAD"))
    private void resetTargetPosition(CallbackInfo ci) {
        ActivationRange.resetTargetPosition((Goal) (Object) this);
    }
}
