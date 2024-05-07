package me.wesley1808.servercore.mixin.features.activation_range.fixes;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(value = PistonMovingBlockEntity.class, priority = 900)
public class PistonMovingBlockEntityMixin {

    // Paper - Fix items getting stuck in slime pushed by a piston
    @Inject(
            method = "moveCollidedEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void servercore$onPushEntity(Level level, BlockPos pos, float f, PistonMovingBlockEntity piston, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        MinecraftServer server = level.getServer();
        if (server != null) {
            final int tick = server.getTickCount() + 10;
            entity.servercore$setActivatedTick(Math.max(entity.servercore$getActivatedTick(), tick));
            entity.servercore$setActivatedImmunityTick(Math.max(entity.servercore$getActivatedImmunityTick(), tick));
        }
    }
}
