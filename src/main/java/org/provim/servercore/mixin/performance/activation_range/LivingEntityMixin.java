package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InactiveEntity {
    @Shadow
    protected int despawnCounter;

    private LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void inactiveTick() {
        this.despawnCounter++;
    }
}
