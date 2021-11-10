package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.provim.servercore.ServerCore;
import org.provim.servercore.interfaces.ActivationEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {

    @Inject(method = "moveCollidedEntities", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"))
    private static void onPushEntity(Level level, BlockPos blockPos, float f, PistonMovingBlockEntity pistonMovingBlockEntity, CallbackInfo ci, Direction direction, double d, VoxelShape voxelShape, AABB aABB, List list, List list2, boolean bl, Iterator var12, Entity entity, Vec3 vec3, double e, double g, double h) {
        final int ticks = ServerCore.getServer().getTickCount() + 10;
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        activationEntity.setActivatedTick(Math.max(activationEntity.getActivatedTick(), ticks));
        activationEntity.setActivatedImmunityTick(Math.max(activationEntity.getActivatedImmunityTick(), ticks));
    }
}
