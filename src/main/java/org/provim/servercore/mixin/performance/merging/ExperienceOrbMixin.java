package org.provim.servercore.mixin.performance.merging;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
    @Shadow
    public int count;

    @Shadow
    public int age;

    @Shadow
    private int value;

    private ExperienceOrbMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * @author Wesley1808
     * @reason Allows experience orbs without the same value to merge.
     */

    @Overwrite
    private static boolean canMerge(ExperienceOrb orb, int seed, int amount) {
        boolean canMerge = !orb.isRemoved() && (orb.getId() - seed) % 40 == 0;
        return FeatureConfig.FAST_XP_MERGING.get() ? canMerge : canMerge && orb.getValue() == amount;
    }

    /**
     * @author Wesley1808
     * @reason Correctly merge experience orbs with different values.
     */

    @Overwrite
    private void merge(ExperienceOrb other) {
        if (FeatureConfig.FAST_XP_MERGING.get()) {
            this.value += other.getValue();
        } else {
            this.count += other.count;
        }
        this.age = Math.min(this.age, other.age);
        other.discard();
    }

    // Configurable experience orb merging radius.
    @Redirect(method = "scanForEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"))
    private AABB setMergeRadius(AABB box, double value) {
        return box.inflate(FeatureConfig.XP_MERGE_RADIUS.get());
    }
}
