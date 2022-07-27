package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.common.config.tables.DynamicBrainActivationConfig;
import me.wesley1808.servercore.common.interfaces.activation_range.IGoalSelector;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * And: Pufferfish (Dynamic-Activation-of-Brain.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(GoalSelector.class)
public abstract class GoalSelectorMixin implements IGoalSelector {
    @Shadow
    @Final
    private Set<WrappedGoal> availableGoals;

    @Unique
    private int curRate;

    @Override
    public boolean hasTasks() {
        for (WrappedGoal task : this.availableGoals) {
            if (task.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean inactiveTick(int tickRate, boolean inactive) {
        final int maxTickRate = DynamicBrainActivationConfig.MAX_ACTIVATION_PRIORITY.get();
        return ++this.curRate % (inactive ? maxTickRate : Math.min(tickRate, maxTickRate)) == 0;
    }
}
