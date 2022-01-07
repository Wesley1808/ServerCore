package me.wesley1808.servercore.mixin.features.merging;

import me.wesley1808.servercore.config.tables.FeatureConfig;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    public int age;

    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At("HEAD"), cancellable = true)
    private static void canMerge(ExperienceOrb experienceOrb, int seed, int value, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!experienceOrb.isRemoved() && (experienceOrb.getId() - seed) % (FeatureConfig.FAST_XP_MERGING.get() ? 8 : 40) == 0 && experienceOrb.getValue() == value);
    }

    // Configurable experience orb merging radius.
    @Redirect(method = "scanForEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"))
    private AABB setMergeRadius(AABB box, double value) {
        return box.inflate(FeatureConfig.XP_MERGE_RADIUS.get());
    }
}
