package org.provim.servercore.mixin.performance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity {
    protected ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "isMergeable(Lnet/minecraft/entity/ExperienceOrbEntity;II)Z", at = @At("HEAD"), cancellable = true)
    private static void canMerge(ExperienceOrbEntity experienceOrb, int seed, int value, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!experienceOrb.isRemoved() && (experienceOrb.getId() - seed) % (FeatureConfig.FAST_XP_MERGING.get() ? 8 : 40) == 0 && experienceOrb.getExperienceAmount() == value);
    }

    @Redirect(method = "expensiveUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box setMergeRadius(Box box, double value) {
        return box.expand(FeatureConfig.XP_MERGE_RADIUS.get());
    }
}
