package org.provim.servercore.mixin.performance.activation_range;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.ServerCore;
import org.provim.servercore.interfaces.ActivationTypeEntity;
import org.provim.servercore.interfaces.InactiveEntity;
import org.provim.servercore.utils.activation_range.ActivationRange;
import org.provim.servercore.utils.activation_range.ActivationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * From: Spigot (Entity-Activation-Range.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Entity.class)
public abstract class EntityMixin implements ActivationTypeEntity, InactiveEntity {
    @Unique
    private int activatedTick = Integer.MIN_VALUE;

    @Unique
    private ActivationType activationType;

    @Unique
    private boolean isExcluded = false;

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void setupActivationStates(EntityType<?> entityType, World world, CallbackInfo ci) {
        this.activationType = ActivationRange.initializeEntityActivationType((Entity) (Object) this);
        this.isExcluded = ActivationRange.isExcluded((Entity) (Object) this);
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;adjustMovementForPiston(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    public void onPistonMove(MovementType movementType, Vec3d vec3d, CallbackInfo ci) {
        this.activatedTick = ServerCore.getServer().getTicks() + 20;
    }

    @Override
    public ActivationType getActivationType() {
        return this.activationType;
    }

    @Override
    public boolean isExcluded() {
        return this.isExcluded;
    }

    @Override
    public int getActivatedTick() {
        return this.activatedTick;
    }

    @Override
    public void setActivatedTick(int activatedTick) {
        this.activatedTick = activatedTick;
    }

    @Override
    public void inactiveTick() {
    }
}
