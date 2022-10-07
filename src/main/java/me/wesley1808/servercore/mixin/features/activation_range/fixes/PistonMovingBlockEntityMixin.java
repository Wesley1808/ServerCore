package me.wesley1808.servercore.mixin.features.activation_range.fixes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(value = PistonMovingBlockEntity.class, priority = 900)
public abstract class PistonMovingBlockEntityMixin {

    // Paper - Fix items getting stuck in slime pushed by a piston
    @Inject(
            method = "moveCollidedEntities",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void servercore$onPushEntity(Level level, BlockPos blockPos, float f, PistonMovingBlockEntity pistonMovingBlockEntity, CallbackInfo ci, Direction direction, double d, VoxelShape voxelShape, AABB aABB, List list, List list2, boolean bl, Iterator var12, Entity entity, Vec3 vec3, double e, double g, double h) {
        MinecraftServer server = level.getServer();
        if (server != null) {
            final int tick = server.getTickCount() + 10;
            entity.setActivatedTick(Math.max(entity.getActivatedTick(), tick));
            entity.setActivatedImmunityTick(Math.max(entity.getActivatedImmunityTick(), tick));
        }
    }
}
