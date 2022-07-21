package me.wesley1808.servercore.mixin.compat.vmp;

import me.wesley1808.servercore.common.interfaces.compat.IMobCounts;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LocalMobCapCalculator.MobCounts.class, priority = 2000)
public abstract class MobCountsMixin implements IMobCounts {
    @Dynamic
    @Shadow
    @Final
    private int[] spawnGroupDensities;

    @Override
    public int getMobcount(MobCategory category) {
        return this.spawnGroupDensities[category.ordinal()];
    }
}
