package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Based on: Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    @Shadow
    @Final
    public GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    private MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void servercore$inactiveTick() {
        super.servercore$inactiveTick();
        this.goalSelector.servercore$inactiveTick();
        this.targetSelector.servercore$inactiveTick();
    }
}
