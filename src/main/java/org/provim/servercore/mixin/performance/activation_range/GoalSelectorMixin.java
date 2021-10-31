package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.provim.servercore.interfaces.IGoalSelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin implements IGoalSelector {
    @Unique
    private int curRate;

    @Shadow
    @Final
    private Set<PrioritizedGoal> goals;

    @Override
    public boolean hasTasks() {
        for (PrioritizedGoal task : this.goals) {
            if (task.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean inactiveTick() {
        return ++this.curRate % 20 == 0;
    }
}
