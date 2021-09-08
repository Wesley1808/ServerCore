package org.provim.servercore.mixin.limits;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.provim.servercore.config.Config;
import org.provim.servercore.utils.TickUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void resetLoveTicks();

    /**
     * Cancels animal breeding if there are too many animals of the same type in the surrounding area.
     */

    @Inject(method = "breed", at = @At(value = "HEAD"), cancellable = true)
    public void cancelBreeding(ServerWorld world, AnimalEntity other, CallbackInfo ci) {
        if (TickUtils.checkForEntities(this, Config.getEntityConfig().animalCount, Config.getEntityConfig().animalRange)) {
            this.setBreedingAge(6000);
            other.setBreedingAge(6000);
            this.resetLoveTicks();
            other.resetLoveTicks();
            ci.cancel();
        }
    }
}