package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.provim.servercore.config.tables.ActivationRangeConfig;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.EntityActivationRange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    public void activateEntities(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (ActivationRangeConfig.ENABLED.get() && this.server.getTicks() % 20 == 0) {
            EntityActivationRange.activateEntities((ServerWorld) (Object) this);
        }
    }

    @Inject(method = "tickEntity", at = @At(value = "HEAD"), cancellable = true)
    public void shouldTickEntity(Entity entity, CallbackInfo ci) {
        if (!EntityActivationRange.isActive(entity)) {
            ((InactiveEntity) entity).inactiveTick();
            entity.age++;
            ci.cancel();
        }
    }

    @Redirect(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickRiding()V"))
    public void shouldTickPassengers(Entity entity) {
        if (EntityActivationRange.isActive(entity)) {
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
}
