package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.activation_range.ActivationRange;
import me.wesley1808.servercore.common.activation_range.ActivationType;
import me.wesley1808.servercore.common.interfaces.activation_range.ActivationEntity;
import me.wesley1808.servercore.common.interfaces.activation_range.Inactive;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Based on: Paper & Spigot (Entity-Activation-Range.patch)
 * <p>
 * Patch Author: Aikar (aikar@aikar.co)
 * <br>
 * License: GPL-3.0 (licenses/GPL.md)
 */
@Mixin(Entity.class)
public class EntityMixin implements Inactive, ActivationEntity {
    @Shadow
    @Final
    private Set<String> tags;

    @Shadow
    private Level level;

    @Unique
    private int servercore$activatedTick = Integer.MIN_VALUE;

    @Unique
    private int servercore$activatedImmunityTick = Integer.MIN_VALUE;

    @Unique
    private boolean servercore$isInactive = false;

    @Unique
    private ActivationType servercore$activationType;

    @Unique
    private boolean servercore$excluded = false;

    @Unique
    private int servercore$fullTickCount;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void servercore$setupActivationStates(EntityType<?> type, Level level, CallbackInfo ci) {
        final Entity entity = (Entity) (Object) this;
        this.servercore$activationType = ActivationRange.initializeEntityActivationType(entity);
        this.servercore$excluded = level == null || ActivationRange.isExcluded(entity);
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
            this.servercore$activatedTick = Math.max(this.servercore$activatedTick, ticks);
            this.servercore$activatedImmunityTick = Math.max(this.servercore$activatedImmunityTick, ticks);
        }
    }

    // ServerCore - Prevent inactive entities from getting extreme velocities.
    @Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
    public void servercore$ignorePushingWhileInactive(double x, double y, double z, CallbackInfo ci) {
        if (this.servercore$isInactive && !this.level.isClientSide) {
            ci.cancel();
        }
    }

    // ServerCore - Add a simple way to exclude certain entities from activation range.
    @Inject(method = "load", at = @At("RETURN"))
    private void servercore$onLoadNbt(CallbackInfo ci) {
        this.servercore$excluded |= this.tags.contains("exclude_ear");
    }

    @Inject(method = "addTag", at = @At("HEAD"))
    private void servercore$onTagAdded(String tag, CallbackInfoReturnable<Boolean> cir) {
        this.servercore$excluded |= tag.equals("exclude_ear");
    }

    @Override
    public ActivationType servercore$getActivationType() {
        return this.servercore$activationType;
    }

    @Override
    public boolean servercore$isExcluded() {
        return this.servercore$excluded;
    }

    @Override
    public int servercore$getActivatedTick() {
        return this.servercore$activatedTick;
    }

    @Override
    public void servercore$setActivatedTick(int tick) {
        this.servercore$activatedTick = tick;
    }

    @Override
    public int servercore$getActivatedImmunityTick() {
        return this.servercore$activatedImmunityTick;
    }

    @Override
    public void servercore$setActivatedImmunityTick(int tick) {
        this.servercore$activatedImmunityTick = tick;
    }

    @Override
    public void servercore$setInactive(boolean inactive) {
        this.servercore$isInactive = inactive;
    }

    @Override
    public int servercore$getFullTickCount() {
        return this.servercore$fullTickCount;
    }

    @Override
    public void servercore$incFullTickCount() {
        this.servercore$fullTickCount++;
    }

    @Override
    public void servercore$inactiveTick() {
        // no-op
    }
}
