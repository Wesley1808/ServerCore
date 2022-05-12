package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SleepInBed.class)
public abstract class SleepInBedMixin {

    // Don't load chunks to find beds
    @Inject(
            method = "checkExtraStartConditions",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;",
                    ordinal = 0
            )
    )
    private void onlyProcessIfLoaded(ServerLevel level, LivingEntity owner, CallbackInfoReturnable<Boolean> cir, Brain<?> brain, GlobalPos globalPos) {
        if (!ChunkManager.isChunkLoaded(level, globalPos.pos())) {
            cir.setReturnValue(false);
        }
    }
}
