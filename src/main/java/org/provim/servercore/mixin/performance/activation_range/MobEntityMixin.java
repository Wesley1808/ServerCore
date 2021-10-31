package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements InactiveEntity {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    private MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void inactiveTick() {
        this.despawnCounter++;
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}
