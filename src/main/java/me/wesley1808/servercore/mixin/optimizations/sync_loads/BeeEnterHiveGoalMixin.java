package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Based on: Paper (Do-not-allow-bees-to-load-chunks-for-beehives.patch)
 * Patch Author: chickeneer (emcchickeneer@gmail.com)
 */

@Mixin(targets = "net/minecraft/world/entity/animal/Bee$BeeEnterHiveGoal")
public class BeeEnterHiveGoalMixin {
    @Shadow
    @Final
    Bee field_20367;

    @Inject(
            method = "start",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void servercore$onlyStartIfLoaded(CallbackInfo ci) {
        // noinspection ConstantConditions
        if (!ChunkManager.hasChunk(this.field_20367.level, this.field_20367.getHivePos())) {
            ci.cancel();
        }
    }

    @Inject(
            method = "canBeeUse",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void servercore$onlyUseIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        // noinspection ConstantConditions
        if (!ChunkManager.hasChunk(this.field_20367.level, this.field_20367.getHivePos())) {
            cir.setReturnValue(false);
        }
    }
}
