package me.wesley1808.servercore.mixin.features.dynamic;

import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobCategory.class, priority = 900)
public class MobCategoryMixin implements IMobCategory {
    @Mutable
    @Shadow
    @Final
    private int max;
    @Unique
    private int servercore$originalCapacity;
    @Unique
    private int servercore$modifiedCapacity;
    @Unique
    private int servercore$spawnInterval;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void servercore$onInit(String enumName, int index, String name, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, CallbackInfo ci) {
        this.servercore$originalCapacity = max;
        this.servercore$modifiedCapacity = max;
        this.servercore$spawnInterval = isPersistent ? 400 : 1;
    }

    // Should be @ModifyReturnValue to remove callback info allocations, however carpet mod.
    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void servercore$getCapacity(CallbackInfoReturnable<Integer> cir) {
        if (this.servercore$modifiedCapacity != this.max) {
            cir.setReturnValue(this.servercore$modifiedCapacity);
        }
    }

    @Override
    public int servercore$getSpawnInterval() {
        return this.servercore$spawnInterval;
    }

    @Override
    public int servercore$getOriginalCapacity() {
        return this.servercore$originalCapacity;
    }

    @Override
    public void servercore$modifyCapacity(double modifier) {
        this.servercore$modifiedCapacity = (int) (this.max * modifier);
    }

    @Override
    public void servercore$modifySpawningConfig(int max, int interval) {
        this.max = max;
        this.servercore$spawnInterval = interval;
    }
}
