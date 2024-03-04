package me.wesley1808.servercore.mixin.features.breeding_cap.tasks;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BreedGoal.class)
public class BreedGoalMixin {
    @Shadow
    @Final
    protected Animal animal;
    @Shadow
    @Nullable
    protected Animal partner;

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/BreedGoal;breed()V"
            )
    )
    private boolean servercore$enforceBreedCap(BreedGoal goal) {
        if (BreedingCap.ANIMAL.exceedsLimit(this.animal)) {
            BreedingCap.resetLove(this.animal, this.partner);
            return false;
        }

        return true;
    }
}
