package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.SpawnDensityCapper;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnDensityCapper.DensityCap.class)
public abstract class DensityCapMixin {
    @Redirect(method = "canSpawn(Lnet/minecraft/entity/SpawnGroup;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/SpawnGroup;getCapacity()I"))
    private int canSpawn(SpawnGroup instance) {
        return TickManager.getMobcap(instance);
    }
}