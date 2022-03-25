package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.world.entity.MobCategory;
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
        final double modifier = TickManager.getMobcapModifier();
        if (modifier != 1.0D) {
            cir.setReturnValue((int) (this.max * modifier));
        }
    }
}
