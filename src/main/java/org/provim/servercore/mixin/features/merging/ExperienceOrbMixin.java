package org.provim.servercore.mixin.features.merging;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    public int count;

    @Shadow
    public int age;

    @Shadow
    private int value;

    @Redirect(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/ExperienceOrb;value:I"))
    private static int canMerge(ExperienceOrb orb, ExperienceOrb experienceOrb, int seed, int value) {
        if (FeatureConfig.FAST_XP_MERGING.get()) {
            return value;
        } else {
            return orb.getValue();
        }
    }

    @Redirect(method = "merge", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/world/entity/ExperienceOrb;count:I"))
    private void merge(ExperienceOrb orb, int newCount, ExperienceOrb other) {
        if (FeatureConfig.FAST_XP_MERGING.get()) {
            this.value += other.getValue();
        } else {
            this.count = newCount;
        }
    }

    // Configurable experience orb merging radius.
    @Redirect(method = "scanForEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"))
    private AABB setMergeRadius(AABB box, double value) {
        return box.inflate(FeatureConfig.XP_MERGE_RADIUS.get());
    }
}
