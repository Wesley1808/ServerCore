package org.provim.servercore.mixin.performance;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.Box;
import org.objectweb.asm.Opcodes;
import org.provim.servercore.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Shadow
    private int amount;

    @Shadow
    private int pickingCount;

    /**
     * Allows experience orbs without the same value to merge.
     */

    @Redirect(method = "isMergeable(Lnet/minecraft/entity/ExperienceOrbEntity;II)Z", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0, target = "Lnet/minecraft/entity/ExperienceOrbEntity;amount:I"))
    private static int isMergeable(ExperienceOrbEntity other, ExperienceOrbEntity orb, int seed, int amount) {
        if (Config.instance().fastXpMerging) {
            return amount;
        } else {
            return orb.getExperienceAmount();
        }
    }

    /**
     * Fast experience pickup speed.
     */

    @Redirect(method = "merge", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, target = "Lnet/minecraft/entity/ExperienceOrbEntity;pickingCount:I"))
    private void merge(ExperienceOrbEntity orb, int value) {
        if (Config.instance().fastXpMerging) {
            this.amount += orb.getExperienceAmount();
        } else {
            this.pickingCount = value;
        }
    }

    /**
     * Allows for customizing the radius experience orbs will attempt to merge at.
     */

    @Redirect(method = "expensiveUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box setMergeRadius(Box box, double value) {
        return box.expand(Config.instance().xpMergeRadius);
    }
}
