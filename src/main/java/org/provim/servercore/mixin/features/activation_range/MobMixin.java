package org.provim.servercore.mixin.features.activation_range;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.activation_range.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements InactiveEntity {
    @Shadow
    @Final
    public GoalSelector targetSelector;

    @Shadow
    @Final
    protected GoalSelector goalSelector;

    private MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        this.noActionTime++;
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}
