package org.provim.servercore.mixin.features.entity_limits;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.provim.servercore.config.tables.EntityConfig;
import org.provim.servercore.utils.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    private AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void resetLove();

    /**
     * Cancels animal breeding if there are too many animals of the same type in the surrounding area.
     */

    @Inject(method = "spawnChildFromBreeding", at = @At(value = "HEAD"), cancellable = true)
    public void cancelBreeding(ServerLevel level, Animal other, CallbackInfo ci) {
        if (TickManager.checkForEntities(this, EntityConfig.ANIMAL_COUNT.get(), EntityConfig.ANIMAL_RANGE.get())) {
            this.setAge(6000);
            other.setAge(6000);
            this.resetLove();
            other.resetLove();
            ci.cancel();
        }
    }
}