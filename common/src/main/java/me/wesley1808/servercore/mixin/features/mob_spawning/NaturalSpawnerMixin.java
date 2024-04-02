package me.wesley1808.servercore.mixin.features.mob_spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = NaturalSpawner.class, priority = 900)
public class NaturalSpawnerMixin {

    @ModifyVariable(method = "spawnForChunk", at = @At("HEAD"), index = 5, argsOnly = true)
    private static boolean servercore$neverSpawnPersistent(boolean shouldSpawnPersistent) {
        return false;
    }

    @ModifyExpressionValue(
            method = "spawnForChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/MobCategory;isPersistent()Z"
            )
    )
    private static boolean servercore$shouldCancelSpawn(boolean isPersistent, ServerLevel level, @Local(ordinal = 0) MobCategory category) {
        if (category.getMaxInstancesPerChunk() <= 0) {
            return true;
        }

        final int interval = IMobCategory.getSpawnInterval(category);
        return interval > 1 && level.getGameTime() % interval != 0;
    }
}