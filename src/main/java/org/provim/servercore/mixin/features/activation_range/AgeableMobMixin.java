package org.provim.servercore.mixin.features.activation_range;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.activation_range.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin extends PathfinderMob implements InactiveEntity {
    private AgeableMobMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        this.noActionTime++;
        ActivationRange.updateAge((AgeableMob) (Object) this);
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}
