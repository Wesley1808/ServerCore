package me.wesley1808.servercore.mixin.features.entity_limits;

import me.wesley1808.servercore.common.config.tables.EntityLimitConfig;
import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
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

    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"), cancellable = true)
    public void servercore$cancelAnimalBreeding(ServerLevel level, Animal other, CallbackInfo ci) {
        if (BreedingCap.exceedsLimit(this, EntityLimitConfig.ANIMAL_COUNT.get(), EntityLimitConfig.ANIMAL_RANGE.get())) {
            this.setAge(6000);
            other.setAge(6000);
            this.resetLove();
            other.resetLove();
            ci.cancel();
        }
    }
}