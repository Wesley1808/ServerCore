package org.provim.servertools.mixin.performance;

import net.minecraft.entity.SpawnGroup;
import org.provim.servertools.utils.TickUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnGroup.class)
public class SpawnGroupMixin {

    @Shadow
    @Final
    private int capacity;

    /**
     * Modifies the capacity of the mobcap.
     */

    @Inject(method = "getCapacity", at = @At("TAIL"), cancellable = true)
    private void modifyCapacity(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) (this.capacity * TickUtils.getModifier()));
    }
}
