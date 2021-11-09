package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.interfaces.ActivationEntity;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    public void activateEntities(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        ActivationRange.activateEntities((ServerWorld) (Object) this);
    }

    @Redirect(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    public void shouldTickEntity(Entity entity) {
        if (!ActivationRange.checkIfActive(entity)) {
            ((InactiveEntity) entity).inactiveTick();
        } else {
            entity.age++;
            entity.tick();
        }
    }

    @Redirect(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickRiding()V"))
    public void shouldTickPassengers(Entity entity) {
        if (ActivationRange.checkIfActive(entity)) {
            entity.age++;
            entity.tickRiding();
        } else {
            entity.setVelocity(Vec3d.ZERO);
            ((InactiveEntity) entity).inactiveTick();
            Entity vehicle = entity.getVehicle();
            if (vehicle != null) {
                vehicle.updatePassengerPosition(entity);
            }
        }
    }

    // Only increase entity age when ticked.
    // Increasing entity age whilst inactive can break entity behavior.
    @Redirect(method = "tickEntity", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "net/minecraft/entity/Entity.age:I"))
    public void cancelEntityAge(Entity entity, int value) {
        ((ActivationEntity) entity).incTickCount();
    }

    @Redirect(method = "tickPassenger", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "net/minecraft/entity/Entity.age:I"))
    public void cancelPassengerAge(Entity passenger, int value) {
        ((ActivationEntity) passenger).incTickCount();
    }
}
