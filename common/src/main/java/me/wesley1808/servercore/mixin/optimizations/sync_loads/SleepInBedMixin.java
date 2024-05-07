package me.wesley1808.servercore.mixin.optimizations.sync_loads;

import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepInBed.class)
public class SleepInBedMixin {

    // Don't load chunks to find beds.
    @Inject(
            method = "checkExtraStartConditions",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;",
                    ordinal = 0
            )
    )
    private void servercore$onlyProcessIfLoaded(ServerLevel level, LivingEntity owner, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) GlobalPos globalPos) {
        if (!ChunkManager.hasChunk(level, globalPos.pos())) {
            cir.setReturnValue(false);
        }
    }
}
