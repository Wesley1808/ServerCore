package org.provim.servercore.mixin.performance.mob_spawning;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.GameRules;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {

    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    ServerWorld world;

    @Shadow
    private boolean spawnAnimals;

    @Shadow
    private boolean spawnMonsters;

    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;Lnet/minecraft/world/SpawnDensityCapper;)Lnet/minecraft/world/SpawnHelper$Info;"))
    private void updateDistanceMap(CallbackInfo ci) {
        if (Config.getFeatureConfig().useDistanceMap && (this.spawnAnimals || this.spawnMonsters) && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            // Update distance map -> by using a constant 10 as view distance we prevent the situations where:
            // 1 - No mobs will spawn because there's another player with mobs 500 blocks away (High view distance).
            // 2 - There will be 140 mobs in a small area because there's another player 50 blocks away (Low view distance).
            ((IThreadedAnvilChunkStorage) this.threadedAnvilChunkStorage).getDistanceMap().update(this.world.getPlayers(), 10);
        }
    }
}
