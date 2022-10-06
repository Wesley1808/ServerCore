package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Based on: Spigot (Entity-Activation-Range.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    protected int noActionTime;

    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        super.inactiveTick();
        this.noActionTime++;
    }
}
