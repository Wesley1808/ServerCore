package me.wesley1808.servercore.mixin.optimizations.chunk_loading;

import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * From: PaperMC (Do-not-allow-bees-to-load-chunks-for-beehives.patch)
 * License: MIT (licenses/MIT.md)
 */

@Mixin(Bee.BeeEnterHiveGoal.class)
public abstract class BeeEnterHiveGoalMixin {
    @Shadow
    @Final
    Bee this$0;

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void onlyStartIfLoaded(CallbackInfo ci) {
        final BlockPos hivePos = this.this$0.getHivePos();
        if (hivePos != null && !ChunkManager.isChunkLoaded(this.this$0.level, hivePos)) {
            ci.cancel();
        }
    }

    @Inject(method = "canBeeUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), cancellable = true)
    private void onlyUseIfLoaded(CallbackInfoReturnable<Boolean> cir) {
        final BlockPos hivePos = this.this$0.getHivePos();
        if (hivePos != null && !ChunkManager.isChunkLoaded(this.this$0.level, hivePos)) {
            cir.setReturnValue(false);
        }
    }
}
