package me.wesley1808.servercore.mixin.features.dynamic_activation;

import me.wesley1808.servercore.common.config.tables.DynamicBrainActivationConfig;
import me.wesley1808.servercore.common.interfaces.activation_range.DynamicActivationEntity;
import me.wesley1808.servercore.common.interfaces.activation_range.IGoalSelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: Pufferfish (Dynamic-Activation-of-Brain.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Mob.class)
public abstract class MobMixin implements DynamicActivationEntity {
    @Unique
    private int activationPriority = DynamicBrainActivationConfig.MAX_ACTIVATION_PRIORITY.get();

    @Redirect(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;tick()V"))
    private void servercore$tickGoalSelector(GoalSelector goalSelector) {
        if (((IGoalSelector) goalSelector).inactiveTick(this.activationPriority, false)) {
            goalSelector.tick();
        }
    }

    @Redirect(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;tickRunningGoals(Z)V"))
    private void servercore$tickGoalSelector(GoalSelector goalSelector, boolean flag) {
        if (((IGoalSelector) goalSelector).inactiveTick(this.activationPriority, false)) {
            goalSelector.tickRunningGoals(flag);
        }
    }

    @Override
    public int getActivationPriority() {
        return this.activationPriority;
    }

    @Override
    public void setActivationPriority(int tickRate) {
        this.activationPriority = tickRate;
    }
}
