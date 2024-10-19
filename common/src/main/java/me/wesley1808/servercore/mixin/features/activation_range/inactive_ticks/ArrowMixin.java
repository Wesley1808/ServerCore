package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Based on: Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(Arrow.class)
public abstract class ArrowMixin extends AbstractArrow {
    private ArrowMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void servercore$inactiveTick() {
        super.servercore$inactiveTick();

        if (this.isInGround()) {
            this.tickDespawn();
        }
    }
}
