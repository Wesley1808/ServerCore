package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.config.tables.ActivationRangeConfig;
import me.wesley1808.servercore.common.interfaces.activation_range.ActivationEntity;
import me.wesley1808.servercore.common.interfaces.activation_range.InactiveEntity;
import me.wesley1808.servercore.common.utils.ActivationRange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    public void activateEntities(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (ActivationRangeConfig.ENABLED.get() && this.server.getTickCount() % 20 == 0) {
            ActivationRange.activateEntities((ServerLevel) (Object) this);
        }
    }

    @Redirect(
            method = "tickNonPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;tick()V"
            )
    )
    public void shouldTickEntity(Entity entity) {
        if (ActivationRange.checkIfActive(entity)) {
            ((ActivationEntity) entity).setInactive(false);
            entity.tickCount++;
            entity.tick();
        } else {
            ((ActivationEntity) entity).setInactive(true);
            ((InactiveEntity) entity).inactiveTick();
        }
    }

    @Redirect(
            method = "tickPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;rideTick()V"
            )
    )
    public void shouldTickPassengers(Entity entity) {
        if (ActivationRange.checkIfActive(entity)) {
            ((ActivationEntity) entity).setInactive(false);
            entity.tickCount++;
            entity.rideTick();
        } else {
            entity.setDeltaMovement(Vec3.ZERO);
            ((ActivationEntity) entity).setInactive(true);
            ((InactiveEntity) entity).inactiveTick();
            Entity vehicle = entity.getVehicle();
            if (vehicle != null) {
                vehicle.positionRider(entity);
            }
        }
    }

    // ServerCore - Only increase tick count when ticked.
    // Increasing the tick count whilst inactive can break entity behavior.
    @Redirect(
            method = {"tickNonPassenger", "tickPassenger"},
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/world/entity/Entity.tickCount:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void redirectTickCount(Entity entity, int value) {
        ((ActivationEntity) entity).incFullTickCount();
    }
}
