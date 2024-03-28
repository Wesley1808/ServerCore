package me.wesley1808.servercore.mixin.features.activation_range;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.wesley1808.servercore.common.activation_range.ActivationRange;
import me.wesley1808.servercore.common.config.legacy.ActivationRangeConfig;
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
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
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
    private void servercore$activateEntities(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (ActivationRangeConfig.ENABLED.get() && this.server.getTickCount() % 20 == 0) {
            ActivationRange.activateEntities((ServerLevel) (Object) this);
        }
    }

    @WrapWithCondition(
            method = "tickNonPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;tick()V"
            )
    )
    private boolean servercore$shouldTickEntity(Entity entity) {
        if (ActivationRange.checkIfActive(entity, this.server.getTickCount())) {
            entity.servercore$setInactive(false);
            entity.tickCount++;
            return true;
        } else {
            entity.servercore$setInactive(true);
            entity.servercore$inactiveTick();
            return false;
        }
    }

    @WrapWithCondition(
            method = "tickPassenger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;rideTick()V"
            )
    )
    private boolean servercore$shouldTickPassenger(Entity passenger, Entity vehicle, Entity ignored) {
        if (ActivationRange.checkIfActive(passenger, this.server.getTickCount())) {
            passenger.servercore$setInactive(false);
            passenger.tickCount++;
            return true;
        } else {
            passenger.setDeltaMovement(Vec3.ZERO);
            passenger.servercore$setInactive(true);
            passenger.servercore$inactiveTick();
            vehicle.positionRider(passenger);
            return false;
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
    private void servercore$redirectTickCount(Entity entity, int value) {
        entity.servercore$incFullTickCount();
    }
}
