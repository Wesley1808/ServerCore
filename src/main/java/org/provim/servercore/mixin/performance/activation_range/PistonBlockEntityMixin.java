package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
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

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {

    @Inject(method = "pushEntities", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"))
    private static void onPistonPush(World world, BlockPos pos, float f, PistonBlockEntity blockEntity, CallbackInfo ci, Direction direction, double d, VoxelShape voxelShape, Box box, List list, List list2, boolean bl, Iterator var12, Entity entity, Vec3d vec3d, double e, double g, double h) {
        final int ticks = ServerCore.getServer().getTicks() + 10;
        final ActivationEntity activationEntity = (ActivationEntity) entity;
        activationEntity.setActivatedTick(Math.max(activationEntity.getActivatedTick(), ticks));
        activationEntity.setActivatedImmunityTick(Math.max(activationEntity.getActivatedImmunityTick(), ticks));
    }
}
