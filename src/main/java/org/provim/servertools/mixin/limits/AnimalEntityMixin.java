package org.provim.servertools.mixin.limits;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.provim.servertools.config.Config;
import org.provim.servertools.utils.TickUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    @Shadow
    public abstract void resetLoveTicks();

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Cancels animal breeding if there are too many animals of the same type in the surrounding area.
     */

    @Inject(method = "breed", at = @At(value = "HEAD"), cancellable = true)
    public void cancelBreeding(ServerWorld world, AnimalEntity other, CallbackInfo ci) {
        if (TickUtils.checkForEntities(this, Config.instance().animalLimit, Config.instance().animalLimitRange)) {
            this.setBreedingAge(6000);
            other.setBreedingAge(6000);
            this.resetLoveTicks();
            other.resetLoveTicks();
            ci.cancel();
        }
    }
}