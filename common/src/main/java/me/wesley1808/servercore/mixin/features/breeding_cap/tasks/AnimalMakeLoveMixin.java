package me.wesley1808.servercore.mixin.features.breeding_cap.tasks;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnimalMakeLove.class)
public class AnimalMakeLoveMixin {

    @WrapWithCondition(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Animal;spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V"
            )
    )
    private boolean servercore$enforceBreedCap(Animal owner, ServerLevel level, Animal mate) {
        if (BreedingCap.ANIMAL.exceedsLimit(owner)) {
            BreedingCap.resetLove(owner, mate);
            return false;
        }

        return true;
    }
}
