package me.wesley1808.servercore.mixin.features.mob_spawning;

import me.wesley1808.servercore.common.interfaces.IMobCategory;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = NaturalSpawner.class, priority = 900)
public class NaturalSpawnerMixin {

    @ModifyVariable(method = "getFilteredSpawningCategories", at = @At("HEAD"), argsOnly = true, name = "spawnPersistent")
    private static boolean servercore$alwaysSpawnPersistent(boolean spawnPersistent) {
        // We control how often persistent mobs spawn through the spawn interval.
        return true;
    }

    @Redirect(
            method = "getFilteredSpawningCategories",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    private static <T> boolean servercore$canSpawn(List<T> spawnCategories, T value, NaturalSpawner.SpawnState state) {
        MobCategory category = (MobCategory) value;
        if (category.getMaxInstancesPerChunk() <= 0) {
            // Mobcap for category is zero.
            return false;
        }

        final int interval = IMobCategory.getSpawnInterval(category);
        if (interval > 1 && state.localMobCapCalculator.chunkMap.level.getGameTime() % interval != 0) {
            // Category has spawn interval that doesn't match the current tick.
            return false;
        }

        spawnCategories.add(value);
        return true;
    }
}