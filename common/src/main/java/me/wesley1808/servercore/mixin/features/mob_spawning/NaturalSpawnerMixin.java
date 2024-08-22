package me.wesley1808.servercore.mixin.features.mob_spawning;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = NaturalSpawner.class, priority = 900)
public class NaturalSpawnerMixin {

    @ModifyVariable(method = "getFilteredSpawningCategories", at = @At("HEAD"), index = 3, argsOnly = true)
    private static boolean servercore$alwaysSpawnPersistent(boolean shouldSpawnPersistent) {
        // We control how often persistent mobs spawn through the spawn interval.
        return true;
    }

    @WrapWithCondition(
            method = "getFilteredSpawningCategories",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    private static boolean servercore$canSpawn(List<MobCategory> list, Object obj, NaturalSpawner.SpawnState state, @Local(ordinal = 0) MobCategory category) {
        if (category.getMaxInstancesPerChunk() <= 0) {
            return false;
        }

        final int interval = IMobCategory.getSpawnInterval(category);
        return interval <= 1 || state.localMobCapCalculator.chunkMap.level.getGameTime() % interval == 0;
    }
}