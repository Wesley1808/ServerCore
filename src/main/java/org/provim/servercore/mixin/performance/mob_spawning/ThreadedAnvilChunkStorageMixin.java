package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.GameRules;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.utils.data.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin implements IThreadedAnvilChunkStorage {
    @Shadow
    @Final
    ServerWorld world;
    private PlayerMobDistanceMap distanceMap = new PlayerMobDistanceMap();

    @Inject(method = "save(Z)V", at = @At("TAIL"))
    private void resetDistanceMap(boolean flush, CallbackInfo ci) {
        if (FeatureConfig.USE_DISTANCE_MAP.get() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            // Resets the PlayerMobDistanceMap whenever the worlds save.
            // The PlayerMobDistanceMap may rarely fail to remove a player object from the HashSet.
            // This causes areas affected by this to be unable to spawn any mobs.
            this.distanceMap = new PlayerMobDistanceMap();
        }
    }

    @Override
    public PlayerMobDistanceMap getDistanceMap() {
        return this.distanceMap;
    }
}
