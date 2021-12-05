package org.provim.servercore.mixin.optimizations.mob_spawning;

import net.minecraft.world.entity.MobCategory;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobCategory.class, priority = 900)
public abstract class MobCategoryMixin {

    @Shadow
    @Final
    private int max;

    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void modifyMobcaps(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(TickManager.getMobcap(this.max));
    }
}
