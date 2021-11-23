package org.provim.servercore.mixin.optimizations.mob_spawning;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.interfaces.IChunkMap;
import org.provim.servercore.utils.data.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements IChunkMap {
    @Shadow
    @Final
    ServerLevel level;

    @Unique
    private PlayerMobDistanceMap distanceMap = new PlayerMobDistanceMap();

    @Inject(method = "saveAllChunks", at = @At("TAIL"))
    private void resetDistanceMap(boolean flush, CallbackInfo ci) {
        if (FeatureConfig.USE_DISTANCE_MAP.get() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
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
