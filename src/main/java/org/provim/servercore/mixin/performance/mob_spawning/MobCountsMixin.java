package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalMobCapCalculator.MobCounts.class)
public abstract class MobCountsMixin {

    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"))
    private int canSpawn(MobCategory category) {
        return TickManager.getMobcap(category);
    }
}