package org.provim.servercore.mixin.performance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.provim.servercore.config.Config;
import org.provim.servercore.mixin.accessor.ExperienceOrbEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity {
    @Shadow
    private int amount;

    @Shadow
    private int pickingCount;

    @Shadow
    private int orbAge;

    protected ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * @author Wesley1808
     * @reason Allows experience orbs without the same value to merge.
     */

    @Overwrite
    private static boolean isMergeable(ExperienceOrbEntity orb, int seed, int amount) {
        boolean bl = !orb.isRemoved() && (orb.getId() - seed) % 40 == 0;
        return Config.getFeatureConfig().fastXpMerging ? bl : bl && orb.getExperienceAmount() == amount;
    }

    /**
     * @author Wesley1808
     * @reason Correctly merge experience orbs with different values.
     */

    @Overwrite
    private void merge(ExperienceOrbEntity other) {
        if (Config.getFeatureConfig().fastXpMerging) {
            this.amount += other.getExperienceAmount();
        } else {
            this.pickingCount += ((ExperienceOrbEntityAccessor) other).getPickingCount();
        }
        this.orbAge = Math.min(this.orbAge, ((ExperienceOrbEntityAccessor) other).getOrbAge());
        other.discard();
    }

    /**
     * Allows for customizing the radius experience orbs will attempt to merge at.
     */

    @Redirect(method = "expensiveUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box setMergeRadius(Box box, double value) {
        return box.expand(Config.getFeatureConfig().xpMergeRadius);
    }
}
