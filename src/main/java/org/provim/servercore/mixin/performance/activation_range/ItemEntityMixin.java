package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements InactiveEntity {
    @Shadow
    private int pickupDelay;

    @Shadow
    private int age;

    private ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            this.pickupDelay--;
        }

        if (this.age != -32768) {
            this.age++;
        }

        if (this.age >= 6000) {
            this.discard();
        }
    }
}
