package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.entity.SpawnGroup;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnGroup.class)
public abstract class SpawnGroupMixin {

    @Shadow
    @Final
    private int capacity;

    // Modifies the capacity of the mobcap.
    @Inject(method = "getCapacity", at = @At("HEAD"), cancellable = true)
    private void modifyCapacity(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) (this.capacity * TickManager.getModifier()));
    }
}
