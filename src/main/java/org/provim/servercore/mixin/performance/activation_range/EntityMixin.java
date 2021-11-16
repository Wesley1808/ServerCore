package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.provim.servercore.ServerCore;
import org.provim.servercore.interfaces.ActivationEntity;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.ActivationRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: PaperMC & Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Entity.class)
public abstract class EntityMixin implements ActivationEntity, InactiveEntity {
    @Shadow
    public Level level;
    @Shadow
    private Vec3 deltaMovement;
    @Unique
    private int activatedTick = Integer.MIN_VALUE;
    @Unique
    private int activatedImmunityTick = Integer.MIN_VALUE;
    @Unique
    private boolean isTemporarilyActive = false;
    @Unique
    private boolean isInactive = false;
    @Unique
    private ActivationRange.ActivationType activationType;
    @Unique
    private boolean excluded = false;
    @Unique
    private int fullTickCount;

    @Shadow
    public abstract void setDeltaMovement(Vec3 vec3);

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void setupActivationStates(EntityType<?> type, Level level, CallbackInfo ci) {
        if (!this.level.isClientSide) {
            final Entity entity = (Entity) (Object) this;
            this.activationType = ActivationRange.initializeEntityActivationType(entity);
            this.excluded = level != null && ActivationRange.isExcluded(entity);
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/Entity;limitPistonMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public void onPistonMove(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        if (!this.level.isClientSide) {
            final int ticks = ServerCore.getServer().getTickCount() + 20;
            this.activatedTick = Math.max(this.activatedTick, ticks);
            this.activatedImmunityTick = Math.max(this.activatedImmunityTick, ticks);
        }
    }

    // Paper - Ignore movement changes while inactive.
    @Inject(method = "move", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/Entity;maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;"), cancellable = true)
    public void ignoreMovementWhileInactive(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        if (this.isTemporarilyActive && !this.level.isClientSide && this.activationType != ActivationRange.ActivationType.MISC && vec3 == this.deltaMovement && moverType == MoverType.SELF) {
            this.setDeltaMovement(Vec3.ZERO);
            this.level.getProfiler().pop();
            ci.cancel();
        }
    }

    // ServerCore - Prevent inactive entities from getting extreme velocities.
    @Inject(method = "push(DDD)V", at = @At(value = "HEAD"), cancellable = true)
    public void ignorePushingWhileInactive(double x, double y, double z, CallbackInfo ci) {
        if (this.isInactive && !this.level.isClientSide) {
            ci.cancel();
        }
    }

    @Override
    public ActivationRange.ActivationType getActivationType() {
        return this.activationType;
    }

    @Override
    public boolean isExcluded() {
        return this.excluded;
    }

    @Override
    public int getActivatedTick() {
        return this.activatedTick;
    }

    @Override
    public void setActivatedTick(int tick) {
        this.activatedTick = tick;
    }

    @Override
    public int getActivatedImmunityTick() {
        return this.activatedImmunityTick;
    }

    public void setActivatedImmunityTick(int tick) {
        this.activatedImmunityTick = tick;
    }

    @Override
    public void setTemporarilyActive(boolean active) {
        this.isTemporarilyActive = active;
    }

    @Override
    public void setInactive(boolean inactive) {
        this.isInactive = inactive;
    }

    @Override
    public int getFullTickCount() {
        return this.fullTickCount;
    }

    @Override
    public void incFullTickCount() {
        this.fullTickCount++;
    }

    @Override
    public void inactiveTick() {
    }
}
