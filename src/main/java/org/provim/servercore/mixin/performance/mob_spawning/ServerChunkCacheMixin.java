package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.provim.servercore.config.tables.FeatureConfig;
import org.provim.servercore.interfaces.IChunkMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {

    @Shadow
    @Final
    public ChunkMap chunkMap;

    @Shadow
    @Final
    ServerLevel level;

    @Shadow
    private boolean spawnFriendlies;

    @Shadow
    private boolean spawnEnemies;

    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;createState(ILjava/lang/Iterable;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;"))
    private void updateDistanceMap(CallbackInfo ci) {
        if (FeatureConfig.USE_DISTANCE_MAP.get() && (this.spawnFriendlies || this.spawnEnemies) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            // Update distance map -> by using a constant 10 as view distance we prevent the situations where:
            // 1 - No mobs will spawn because there's another player with mobs 500 blocks away (High view distance).
            // 2 - There will be 140 mobs in a small area because there's another player 50 blocks away (Low view distance).
            ((IChunkMap) this.chunkMap).getDistanceMap().update(this.level.players(), 10);
        }
    }
}
