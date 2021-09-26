package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.IThreadedAnvilChunkStorage;
import org.provim.servercore.mixin.accessor.SpawnHelperAccessor;
import org.provim.servercore.mixin.accessor.SpawnHelperInfoAccessor;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {
    @Unique
    private static Chunk cachedChunk;

    @Shadow
    @Final
    private static SpawnGroup[] SPAWNABLE_GROUPS;

    private SpawnHelperMixin() {
    }

    @Shadow
    private static BlockPos getSpawnPos(World world, WorldChunk worldChunk) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean isAcceptableSpawnPosition(ServerWorld serverWorld, Chunk chunk, BlockPos.Mutable mutable, double d) {
        throw new AssertionError();
    }

    @Shadow
    private static MobEntity createMob(ServerWorld serverWorld, EntityType<?> entityType) {
        return null;
    }

    @Shadow
    static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean isValidSpawn(ServerWorld world, MobEntity entity, double squaredDistance) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean canSpawn(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
        throw new AssertionError();
    }

    @Shadow
    private static Optional<SpawnSettings.SpawnEntry> pickRandomSpawnEntry(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, Random random, BlockPos pos) {
        throw new AssertionError();
    }

    /**
     * Replaces the vanilla spawn mechanics with per-player mob spawning.
     * This is not an @Overwrite to keep compatibility with Carpet mod.
     * When using Carpet mod it is however required to disable lagFreeSpawning.
     *
     * @param world: The world to spawn mobs in
     * @param chunk: The chunk to spawn mobs in
     * @param info:  The spawn info
     */

    @Inject(method = "spawn", at = @At(value = "HEAD", shift = At.Shift.AFTER), cancellable = true)
    private static void spawn(ServerWorld world, WorldChunk chunk, SpawnHelper.Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rareSpawn, CallbackInfo ci) {
        world.getProfiler().push("spawner");
        SpawnHelperInfoAccessor spawnInfo = (SpawnHelperInfoAccessor) info;
        IThreadedAnvilChunkStorage chunkStorage = (IThreadedAnvilChunkStorage) world.getChunkManager().threadedAnvilChunkStorage;
        for (SpawnGroup spawnGroup : SPAWNABLE_GROUPS) {
            int difference;
            if (Config.getFeatureConfig().perPlayerSpawns) {
                int minDiff = Integer.MAX_VALUE;
                for (ServerPlayerEntity player : chunkStorage.getDistanceMap().getPlayersInRange(chunk.getPos())) {
                    minDiff = Math.min(spawnGroup.getCapacity() - chunkStorage.getMobCountNear(player, spawnGroup), minDiff);
                }
                difference = (minDiff == Integer.MAX_VALUE) ? 0 : minDiff;
            } else {
                var currEntityCount = info.getGroupToCount().getInt(spawnGroup);
                int capacity = spawnGroup.getCapacity() * ((SpawnHelperInfoAccessor) info).getSpawnChunkCount() / SpawnHelperAccessor.getChunkArea();
                difference = capacity - currEntityCount;
            }

            if ((spawnAnimals || !spawnGroup.isPeaceful()) && (spawnMonsters || spawnGroup.isPeaceful()) && (rareSpawn || !spawnGroup.isRare()) && difference > 0) {
                spawnEntitiesInChunk(spawnGroup, world, chunk, spawnInfo::check, spawnInfo::runMob, difference, Config.getFeatureConfig().perPlayerSpawns ? chunkStorage::updateMobCounts : null);
            }
        }

        world.getProfiler().pop();
        ci.cancel();
    }

    /**
     * [VanillaCopy]
     * Adds {@param trackEntity} and {@param maxSpawns}.
     * Stops spawn attempts in unloaded chunks and
     * caches that chunk for faster biome lookups.
     */

    private static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner, int maxSpawns, Consumer<Entity> trackEntity) {
        BlockPos pos = getSpawnPos(world, chunk);
        int i = pos.getY();
        if (i >= world.getBottomY() + 1) {
            StructureAccessor structureAccessor = world.getStructureAccessor();
            ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
            BlockState blockState = chunk.getBlockState(pos);
            if (!blockState.isSolidBlock(chunk, pos)) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                int j = 0;

                for (int k = 0; k < 3; ++k) {
                    int l = pos.getX();
                    int m = pos.getZ();
                    SpawnSettings.SpawnEntry spawnEntry = null;
                    EntityData entityData = null;
                    int o = MathHelper.ceil(world.random.nextFloat() * 4.0F);
                    int p = 0;

                    for (int q = 0; q < o; ++q) {
                        l += world.random.nextInt(6) - world.random.nextInt(6);
                        m += world.random.nextInt(6) - world.random.nextInt(6);
                        mutable.set(l, i, m);
                        double d = l + 0.5D;
                        double e = m + 0.5D;
                        PlayerEntity playerEntity = world.getClosestPlayer(d, i, e, -1.0D, false);
                        if (playerEntity != null) {
                            double f = playerEntity.squaredDistanceTo(d, i, e);
                            if (isAcceptableSpawnPosition(world, chunk, mutable, f) && (cachedChunk = ChunkManager.getChunkIfLoaded(world, mutable)) != null) {
                                if (spawnEntry == null) {
                                    Optional<SpawnSettings.SpawnEntry> optional = pickRandomSpawnEntry(world, structureAccessor, chunkGenerator, group, world.random, mutable);
                                    if (optional.isEmpty()) {
                                        break;
                                    }

                                    spawnEntry = optional.get();
                                    o = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
                                }

                                if (canSpawn(world, group, structureAccessor, chunkGenerator, spawnEntry, mutable, f) && checker.test(spawnEntry.type, mutable, chunk)) {
                                    MobEntity mobEntity = createMob(world, spawnEntry.type);
                                    if (mobEntity == null) {
                                        return;
                                    }

                                    mobEntity.refreshPositionAndAngles(d, i, e, world.random.nextFloat() * 360.0F, 0.0F);
                                    if (isValidSpawn(world, mobEntity, f)) {
                                        entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.NATURAL, entityData, null);
                                        ++j;
                                        ++p;
                                        world.spawnEntityAndPassengers(mobEntity);
                                        runner.run(mobEntity, chunk);

                                        if (trackEntity != null) {
                                            trackEntity.accept(mobEntity);
                                        }

                                        if (j >= mobEntity.getLimitPerChunk() || j >= maxSpawns) {
                                            return;
                                        }

                                        if (mobEntity.spawnsTooManyForEachTry(p)) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Use cached chunk.
    @Redirect(method = "getSpawnEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private static BlockState useCachedChunk(ServerWorld serverWorld, BlockPos pos) {
        return cachedChunk.getBlockState(pos);
    }

    // Fast biome lookups.
    @Redirect(method = "getSpawnEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
    private static Biome fastBiomeLookup$1(ServerWorld serverWorld, BlockPos pos) {
        return getBiomeDirectly(pos, cachedChunk);
    }

    @Redirect(method = "pickRandomSpawnEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
    private static Biome fastBiomeLookup$2(ServerWorld serverWorld, BlockPos pos) {
        return getBiomeDirectly(pos, cachedChunk);
    }
}