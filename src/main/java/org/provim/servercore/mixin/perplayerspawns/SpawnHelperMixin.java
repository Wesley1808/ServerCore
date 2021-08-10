package org.provim.servercore.mixin.perplayerspawns;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.provim.servercore.config.Config;
import org.provim.servercore.interfaces.TACSInterface;
import org.provim.servercore.mixin.accessor.SpawnHelperAccessor;
import org.provim.servercore.mixin.accessor.SpawnHelperInfoAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {

    @Shadow
    @Final
    private static SpawnGroup[] SPAWNABLE_GROUPS;

    private SpawnHelperMixin() {
    }

    @Shadow
    private static BlockPos getSpawnPos(World world, WorldChunk worldChunk) {
        return null;
    }

    @Shadow
    private static boolean isAcceptableSpawnPosition(ServerWorld serverWorld, Chunk chunk, BlockPos.Mutable mutable, double d) {
        return false;
    }

    @Shadow
    private static MobEntity createMob(ServerWorld serverWorld, EntityType<?> entityType) {
        return null;
    }

    @Shadow
    public static boolean canSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Shadow
    static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        return null;
    }

    @Shadow
    private static boolean isValidSpawn(ServerWorld world, MobEntity entity, double squaredDistance) {
        return false;
    }

    @Shadow
    private static Pool<SpawnSettings.SpawnEntry> getSpawnEntries(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, @Nullable Biome biome) {
        return null;
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

    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    private static void spawn(ServerWorld world, WorldChunk chunk, SpawnHelper.Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rareSpawn, CallbackInfo ci) {
        world.getProfiler().push("spawner");
        SpawnHelperInfoAccessor spawnInfo = (SpawnHelperInfoAccessor) info;
        TACSInterface chunkStorage = (TACSInterface) world.getChunkManager().threadedAnvilChunkStorage;
        for (SpawnGroup spawnGroup : SPAWNABLE_GROUPS) {
            int difference;
            if (Config.instance().perPlayerSpawns) {
                int minDiff = Integer.MAX_VALUE;
                for (ServerPlayerEntity player : chunkStorage.getPlayerMobDistanceMap().getPlayersInRange(chunk.getPos())) {
                    minDiff = Math.min(spawnGroup.getCapacity() - chunkStorage.getMobCountNear(player, spawnGroup), minDiff);
                }
                difference = (minDiff == Integer.MAX_VALUE) ? 0 : minDiff;
            } else {
                var currEntityCount = info.getGroupToCount().getInt(spawnGroup);
                int k1 = spawnGroup.getCapacity() * ((SpawnHelperInfoAccessor) info).getSpawnChunkCount() / SpawnHelperAccessor.getChunkArea();
                difference = k1 - currEntityCount;
            }

            if ((spawnAnimals || !spawnGroup.isPeaceful()) && (spawnMonsters || spawnGroup.isPeaceful()) && (rareSpawn || !spawnGroup.isRare()) && difference > 0) {
                spawnEntitiesInChunk(spawnGroup, world, chunk, spawnInfo::check, spawnInfo::runMob, difference, Config.instance().perPlayerSpawns ? chunkStorage::updateMobCounts : null);
            }
        }

        world.getProfiler().pop();
        ci.cancel();
    }

    /**
     * [VanillaCopy]
     * Adds {@param trackEntity} to add to nearby player mob counts.
     * Optimizes & reduces calls to getBiome(), getSpawnEntries().
     */

    private static void spawnEntitiesInChunk(SpawnGroup spawnGroup, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner, int maxSpawns, Consumer<Entity> trackEntity) {
        var pos = getSpawnPos(world, chunk);
        if (pos.getY() >= world.getBottomY() + 1) {
            var chunkGenerator = world.getChunkManager().getChunkGenerator();
            var structureAccessor = world.getStructureAccessor();
            int y = pos.getY();
            var spawns = 0;
            var blockState = chunk.getBlockState(pos);
            if (blockState != null && !blockState.isSolidBlock(chunk, pos)) {
                var mutable = new BlockPos.Mutable();
                for (var i = 0; i < 3; i++) {
                    int l = pos.getX();
                    int m = pos.getZ();
                    SpawnSettings.SpawnEntry spawnEntry;
                    EntityData entityData = null;
                    int o = MathHelper.ceil(world.random.nextFloat() * 4.0F);
                    var p = 0;

                    for (var j = 0; j < o; ++j) {
                        l += world.random.nextInt(6) - world.random.nextInt(6);
                        m += world.random.nextInt(6) - world.random.nextInt(6);
                        mutable.set(l, y, m);
                        double x = l + 0.5D;
                        double z = m + 0.5D;
                        double distance = getDistanceToClosestPlayer(world, x, y, z);

                        if (distance != Double.MAX_VALUE) {
                            if (isAcceptableSpawnPosition(world, chunk, mutable, distance)) {
                                var biome = getBiomeDirectly(mutable, chunk);
                                Pool<SpawnSettings.SpawnEntry> spawnEntries = getSpawnEntries(world, structureAccessor, chunkGenerator, spawnGroup, mutable, biome);
                                Optional<SpawnSettings.SpawnEntry> optional = pickRandomSpawnEntry(biome, spawnGroup, world.random, spawnEntries);
                                if (optional.isEmpty()) {
                                    break;
                                }

                                spawnEntry = optional.get();
                                o = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);

                                if (canSpawn(world, spawnEntries, spawnEntry, mutable, distance) && checker.test(spawnEntry.type, mutable, chunk)) {
                                    var mobEntity = createMob(world, spawnEntry.type);
                                    if (mobEntity == null) {
                                        return;
                                    }

                                    mobEntity.refreshPositionAndAngles(x, y, z, world.random.nextFloat() * 360.0F, 0.0F);
                                    if (isValidSpawn(world, mobEntity, distance)) {
                                        entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.NATURAL, entityData, null);
                                        spawns++;
                                        p++;
                                        world.spawnEntityAndPassengers(mobEntity);
                                        runner.run(mobEntity, chunk);

                                        if (trackEntity != null) {
                                            trackEntity.accept(mobEntity);
                                        }

                                        if (spawns >= mobEntity.getLimitPerChunk() || spawns >= maxSpawns) {
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

    private static double getDistanceToClosestPlayer(ServerWorld world, double x, double y, double z) {
        double distance = Double.MAX_VALUE;
        for (ServerPlayerEntity player : world.getPlayers()) {
            distance = player.interactionManager.getGameMode() != GameMode.SPECTATOR ? Math.min(distance, player.squaredDistanceTo(x, y, z)) : distance;
        }
        return distance;
    }

    // Removes duplicate call to getSpawnEntries().
    private static boolean canSpawn(ServerWorld world, Pool<SpawnSettings.SpawnEntry> spawnEntries, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
        EntityType<?> entityType = spawnEntry.type;
        if (entityType.getSpawnGroup() == SpawnGroup.MISC) {
            return false;
        } else if (!entityType.isSpawnableFarFromPlayer() && squaredDistance > (entityType.getSpawnGroup().getImmediateDespawnRange() * entityType.getSpawnGroup().getImmediateDespawnRange())) {
            return false;
        } else if (entityType.isSummonable() && containsSpawnEntry(spawnEntries, spawnEntry)) {
            var location = SpawnRestriction.getLocation(entityType);
            if (!canSpawn(location, world, pos, entityType)) {
                return false;
            } else if (!SpawnRestriction.canSpawn(entityType, world, SpawnReason.NATURAL, pos, world.random)) {
                return false;
            } else {
                return world.isSpaceEmpty(entityType.createSimpleBoundingBox(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
            }
        } else {
            return false;
        }
    }

    private static boolean containsSpawnEntry(Pool<SpawnSettings.SpawnEntry> spawnEntries, SpawnSettings.SpawnEntry spawnEntry) {
        return spawnEntries.getEntries().contains(spawnEntry);
    }

    // Removes duplicate call to getSpawnEntries().
    private static Optional<SpawnSettings.SpawnEntry> pickRandomSpawnEntry(Biome biome, SpawnGroup spawnGroup, Random random, Pool<SpawnSettings.SpawnEntry> spawnEntries) {
        return spawnGroup == SpawnGroup.WATER_AMBIENT && biome.getCategory() == Biome.Category.RIVER && random.nextFloat() < 0.98F ? Optional.empty() : spawnEntries.getOrEmpty(random);
    }
}