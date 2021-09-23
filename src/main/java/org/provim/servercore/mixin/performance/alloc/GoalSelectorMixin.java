package org.provim.servercore.mixin.performance.alloc;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Set;

/**
 * From: PaperMC
 * https://github.com/PaperMC/Paper/blob/master/patches/server/0400-Remove-streams-from-Mob-AI-System.patch
 */

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin {
    @Shadow
    @Final
    private Set<PrioritizedGoal> goals;

    /**
     * @author Wesley1808
     * @reason Avoid stream allocations.
     */

    @Overwrite
    public void remove(Goal goal) {
        for (Iterator<PrioritizedGoal> iterator = this.goals.iterator(); iterator.hasNext(); ) {
            PrioritizedGoal prioritizedGoal = iterator.next();
            if (prioritizedGoal.getGoal() != goal) {
                continue;
            }

            if (prioritizedGoal.isRunning()) {
                prioritizedGoal.stop();
            }

            iterator.remove();
        }
    }
}
