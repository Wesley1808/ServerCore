package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Based on: Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity {
    @Shadow
    private int waitTime;

    @Shadow
    private int duration;

    private AreaEffectCloudMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void servercore$inactiveTick() {
        super.servercore$inactiveTick();

        if (++this.tickCount >= this.waitTime + this.duration) {
            this.discard();
        }
    }
}
