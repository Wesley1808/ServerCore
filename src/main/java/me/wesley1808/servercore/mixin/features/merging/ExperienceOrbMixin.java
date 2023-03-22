package me.wesley1808.servercore.mixin.features.merging;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

    @ModifyConstant(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", constant = @Constant(intValue = 40), require = 0)
    private static int servercore$modifyMergeChance(int constant) {
        return FeatureConfig.XP_MERGE_CHANCE.get();
    }

    @ModifyArg(
            method = "scanForEntities",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private double servercore$modifyMergeRadius(double value) {
        return FeatureConfig.XP_MERGE_RADIUS.get();
    }
}
