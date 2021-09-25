package org.provim.servercore.mixin.perplayerspawns;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GravityField;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.ChunkManager;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.IServerPlayerEntity;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.mixin.accessor.SpawnHelperAccessor;
import org.provim.servercore.mixin.accessor.SpawnHelperInfoAccessor;
import org.provim.servercore.utils.patches.PlayerMobDistanceMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;


@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin extends ChunkManager {

    @Shadow
    @Final
    public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Shadow
    @Final
    ServerWorld world;

    /**
     * Updates player-mob-distance maps and recalculates mob counts for each player.
     */

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;)Lnet/minecraft/world/SpawnHelper$Info;"))
    private SpawnHelper.Info updateSpawnHelper(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource) {
        SpawnHelper.Info spawnHelperInfo;
        PlayerMobDistanceMap distanceMap = ((IThreadedAnvilChunkStorage) threadedAnvilChunkStorage).getDistanceMap();
        if (distanceMap != null && Config.getFeatureConfig().perPlayerSpawns) {
            // Update distance map -> by using a constant 10 as view distance we prevent the situations where:
            // 1 - No mobs will spawn because there's another player with mobs 500 blocks away (High view distance).
            // 2 - There will be 140 mobs in a small area because there's another player 50 blocks away (Low view distance).

            distanceMap.update(this.world.getPlayers(), 10);
            // Re-set mob counts
            for (ServerPlayerEntity player : this.world.getPlayers()) {
                Arrays.fill(((IServerPlayerEntity) player).getMobCounts(), 0);
            }

            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, chunkSource, true);
        } else {
            spawnHelperInfo = setupSpawn(spawningChunkCount, entities, chunkSource, false);
        }
        return spawnHelperInfo;
    }

    private SpawnHelper.Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, SpawnHelper.ChunkSource chunkSource, boolean countMobs) {
        IThreadedAnvilChunkStorage chunkStorage = (IThreadedAnvilChunkStorage) this.world.getChunkManager().threadedAnvilChunkStorage;
        Object2IntOpenHashMap<SpawnGroup> groupToCount = new Object2IntOpenHashMap<>();
        GravityField gravityField = new GravityField();

        for (Entity entity : entities) {
            SpawnGroup group = entity.getType().getSpawnGroup();
            if (group != SpawnGroup.MISC && entity instanceof MobEntity mob && !(mob.isPersistent() || mob.cannotDespawn())) {
                BlockPos pos = entity.getBlockPos();
                long l = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
                chunkSource.query(l, chunk -> {
                    SpawnSettings.SpawnDensity spawnDensity = SpawnHelperAccessor.getBiome(pos, chunk).getSpawnSettings().getSpawnDensity(entity.getType());
                    if (spawnDensity != null) {
                        gravityField.addPoint(entity.getBlockPos(), spawnDensity.getMass());
                    }

                    groupToCount.addTo(group, 1);
                    if (countMobs) {
                        chunkStorage.updateMobCounts(entity);
                    }
                });
            }
        }
        return SpawnHelperInfoAccessor.init(spawningChunkCount, groupToCount, gravityField);
    }
}