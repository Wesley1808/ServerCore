package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements InactiveEntity {
    private PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void inactiveTick() {
        this.despawnCounter++;
        ActivationRange.updateBreedingAge((PassiveEntity) (Object) this);
        ActivationRange.updateGoalSelectors(this.goalSelector, this.targetSelector);
    }
}
