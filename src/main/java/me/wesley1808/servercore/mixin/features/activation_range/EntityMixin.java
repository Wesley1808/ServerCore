package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.interfaces.activation_range.ActivationEntity;
import me.wesley1808.servercore.common.utils.ActivationRange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Entity.class)
public abstract class EntityMixin implements ActivationEntity {
    @Shadow
    public Level level;

    @Unique
    private int activatedTick = Integer.MIN_VALUE;

    @Unique
    private int activatedImmunityTick = Integer.MIN_VALUE;

    @Unique
    private boolean isInactive = false;

    @Unique
    private ActivationRange.ActivationType activationType;

    @Unique
    private boolean excluded = false;

    @Unique
    private int fullTickCount;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void servercore$setupActivationStates(EntityType<?> type, Level level, CallbackInfo ci) {
        final Entity entity = (Entity) (Object) this;
        this.activationType = ActivationRange.initializeEntityActivationType(entity);
        this.excluded = level == null || ActivationRange.isExcluded(entity);
    }

    @Inject(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;limitPistonMovement(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    shift = At.Shift.BEFORE
            )
    )
    public void servercore$onPistonMove(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        MinecraftServer server = this.level.getServer();
        if (server != null) {
            final int ticks = server.getTickCount() + 20;
            this.activatedTick = Math.max(this.activatedTick, ticks);
            this.activatedImmunityTick = Math.max(this.activatedImmunityTick, ticks);
        }
    }

    // ServerCore - Prevent inactive entities from getting extreme velocities.
    @Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
    public void servercore$ignorePushingWhileInactive(double x, double y, double z, CallbackInfo ci) {
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

    @Override
    public void setActivatedImmunityTick(int tick) {
        this.activatedImmunityTick = tick;
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
}
