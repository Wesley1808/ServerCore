package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.provim.servercore.interfaces.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements InactiveEntity {
    @Shadow
    private int itemAge;

    private ItemEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void inactiveTick() {
        if (this.itemAge != -32768) {
            this.itemAge++;
        }

        if (this.itemAge >= 6000) {
            this.discard();
        }
    }
}
