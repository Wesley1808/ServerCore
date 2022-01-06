package org.provim.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.activation_range.InactiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity implements InactiveEntity {
    @Shadow
    private int waitTime;

    @Shadow
    private int duration;

    private AreaEffectCloudMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        if (++this.tickCount >= this.waitTime + this.duration) {
            this.discard();
        }
    }
}
