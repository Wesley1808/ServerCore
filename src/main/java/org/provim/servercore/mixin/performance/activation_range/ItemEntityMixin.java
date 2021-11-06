package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements InactiveEntity {
    @Shadow
    private int itemAge;

    @Shadow
    private int pickupDelay;

    private ItemEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    // ServerCore - Fixes items not always falling to the ground.
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getId()I"))
    private int redirectId(ItemEntity item) {
        return 1;
    }

    @Override
    public void inactiveTick() {
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            this.pickupDelay--;
        }

        if (this.itemAge != -32768) {
            this.itemAge++;
        }

        if (this.itemAge >= 6000) {
            this.discard();
        }
    }
}
