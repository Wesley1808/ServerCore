package me.wesley1808.servercore.mixin.features.dynamic;

import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobCategory.class, priority = 900)
public class MobCategoryMixin implements IMobCategory {
    @Shadow
    @Final
    private int max;
    @Unique
    private int capacity;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(String enumName, int index, String name, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, CallbackInfo ci) {
        this.capacity = max;
    }

    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void servercore$modifyMobcaps(CallbackInfoReturnable<Integer> cir) {
        if (this.capacity != this.max) {
            cir.setReturnValue(this.capacity);
        }
    }

    @Override
    public void modifyMobcaps(double modifier) {
        this.capacity = (int) (this.max * modifier);
    }
}
