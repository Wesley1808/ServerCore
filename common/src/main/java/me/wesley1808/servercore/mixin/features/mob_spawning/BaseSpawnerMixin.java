package me.wesley1808.servercore.mixin.features.mob_spawning;

import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {
    @Shadow
    protected abstract void delay(Level level, BlockPos blockPos);

    @Inject(
            method = "serverTick",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    ordinal = 0
            )
    )
    private void servercore$enforceMobcap(ServerLevel level, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        boolean canSpawn = Mobcaps.canSpawnForCategory(
                level,
                entity.chunkPosition(),
                entity.getType().getCategory(),
                Config.get().mobSpawning().monsterSpawner()
        );

        if (!canSpawn) {
            this.delay(level, pos);
            ci.cancel();
        }
    }
}
