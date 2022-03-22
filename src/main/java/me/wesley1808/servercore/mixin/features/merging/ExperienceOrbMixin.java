package me.wesley1808.servercore.mixin.features.merging;

import me.wesley1808.servercore.config.tables.FeatureConfig;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    public int age;

    @ModifyConstant(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", constant = @Constant(intValue = 40), require = 0)
    private static int modifyMergeChance(int constant) {
        return FeatureConfig.FAST_XP_MERGING.get() ? 8 : constant;
    }

    @ModifyConstant(method = "scanForEntities", constant = @Constant(doubleValue = 0.5), require = 0)
    private double modifyMergeRadius(double constant) {
        return FeatureConfig.XP_MERGE_RADIUS.get();
    }
}
