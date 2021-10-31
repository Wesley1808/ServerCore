package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements InactiveEntity {
    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract int getBreedingAge();

    @Shadow
    public abstract void setBreedingAge(int i);

    @Override
    public void inactiveTick() {
        ++this.despawnCounter;
        int i = this.getBreedingAge();
        if (i < 0) {
            this.setBreedingAge(i + 1);
        } else if (i > 0) {
            this.setBreedingAge(i - 1);
        }
    }
}
