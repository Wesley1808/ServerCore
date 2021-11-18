package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    public World world;
    @Unique
    private int activatedTick = Integer.MIN_VALUE;
    @Unique
    private int activatedImmunityTick = Integer.MIN_VALUE;
    @Unique
    private ActivationRange.ActivationType activationType;
    @Unique
    private boolean isTemporarilyActive = false;
    @Unique
    private boolean excluded = false;
    @Unique
    private boolean inactive = false;
    @Shadow
    private Vec3d velocity;
    @Unique
    private int tickCount;

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void setupActivationStates(EntityType<?> entityType, World world, CallbackInfo ci) {
        if (!this.world.isClient) {
            final Entity entity = (Entity) (Object) this;
            this.activationType = ActivationRange.initializeEntityActivationType(entity);
            this.excluded = world != null && ActivationRange.isExcluded(entity);
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/Entity;adjustMovementForPiston(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    public void onPistonMove(MovementType movementType, Vec3d vec3d, CallbackInfo ci) {
        if (!this.world.isClient) {
            final int ticks = ServerCore.getServer().getTicks() + 20;
            this.activatedTick = Math.max(this.activatedTick, ticks);
            this.activatedImmunityTick = Math.max(this.activatedImmunityTick, ticks);
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/Entity;adjustMovementForSneaking(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/MovementType;)Lnet/minecraft/util/math/Vec3d;"), cancellable = true)
    public void ignoreMovementWhileInactive(MovementType movementType, Vec3d vec3d, CallbackInfo ci) {
        if (this.isTemporarilyActive && !this.world.isClient && this.activationType != ActivationRange.ActivationType.MISC && vec3d == this.velocity && movementType == MovementType.SELF) {
            this.setVelocity(Vec3d.ZERO);
            this.world.getProfiler().pop();
            ci.cancel();
        }
    }

    // ServerCore - Prevent inactive entities from getting extreme velocities.
    @Inject(method = "addVelocity", at = @At(value = "HEAD"), cancellable = true)
    public void ignorePushingWhileInactive(double x, double y, double z, CallbackInfo ci) {
        if (this.inactive && !this.world.isClient) {
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
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public void incTickCount() {
        this.tickCount++;
    }

    @Override
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    @Override
    public void setTemporarilyActive(boolean active) {
        this.isTemporarilyActive = active;
    }

    @Override
    public void inactiveTick() {
    }
}
