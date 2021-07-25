package org.provim.servercore.utils.perplayerspawns;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.provim.servercore.interfaces.ServerPlayerEntityInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spottedleaf
 */

public final class PlayerMobDistanceMap {

    private static final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> EMPTY_SET = new PooledHashSets.PooledObjectLinkedOpenHashSet<>();

    private final Map<ServerPlayerEntity, ChunkSectionPos> players = new HashMap<>();
    // we use linked for better iteration.
    private final Long2ObjectOpenHashMap<PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity>> playerMap = new Long2ObjectOpenHashMap<>(32, 0.5f);

    private final PooledHashSets<ServerPlayerEntity> pooledHashSets = new PooledHashSets<>();

    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getPlayersInRange(final ChunkPos chunkPos) {
        return this.getPlayersInRange(chunkPos.x, chunkPos.z);
    }

    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> getPlayersInRange(final int chunkX, final int chunkZ) {
        return this.playerMap.getOrDefault(ChunkPos.toLong(chunkX, chunkZ), EMPTY_SET);
    }

    public void update(final List<ServerPlayerEntity> currentPlayers) {
        final ObjectLinkedOpenHashSet<ServerPlayerEntity> gone = new ObjectLinkedOpenHashSet<>(this.players.keySet());

        for (final ServerPlayerEntity player : currentPlayers) {
            if (player.isSpectator()) {
                continue; // will be left in 'gone' (or not added at all)
            }

            gone.remove(player);

            final ChunkSectionPos newPosition = player.getWatchedSection();
            final ChunkSectionPos oldPosition = this.players.put(player, newPosition);

            if (oldPosition == null) {
                this.addNewPlayer(player, newPosition);
            } else {
                this.updatePlayer(player, oldPosition, newPosition);
            }
        }

        for (final ServerPlayerEntity player : gone) {
            final ChunkSectionPos oldPosition = this.players.remove(player);
            if (oldPosition != null) {
                this.removePlayer(player, oldPosition);
            }
        }
    }

    private void addPlayerTo(final ServerPlayerEntity player, final int chunkX, final int chunkZ) {
        this.playerMap.compute(ChunkPos.toLong(chunkX, chunkZ), (final Long key, final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> playerSet) -> {
            if (playerSet == null) {
                return ((ServerPlayerEntityInterface) player).getCachedSingleMobDistanceMap();
            } else {
                return PlayerMobDistanceMap.this.pooledHashSets.findMapWith(playerSet, player);
            }
        });
    }

    private void removePlayerFrom(final ServerPlayerEntity player, final int chunkX, final int chunkZ) {
        this.playerMap.compute(ChunkPos.toLong(chunkX, chunkZ), (final Long keyInMap, final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayerEntity> playerSet) -> {
            return PlayerMobDistanceMap.this.pooledHashSets.findMapWithout(playerSet, player); // rets null instead of an empty map
        });
    }

    private void updatePlayer(final ServerPlayerEntity player, final ChunkSectionPos oldPosition, final ChunkSectionPos newPosition) {
        final int toX = newPosition.getX();
        final int toZ = newPosition.getZ();
        final int fromX = oldPosition.getX();
        final int fromZ = oldPosition.getZ();

        final int dx = toX - fromX;
        final int dz = toZ - fromZ;

        final int totalX = Math.abs(fromX - toX);
        final int totalZ = Math.abs(fromZ - toZ);

        if (Math.max(totalX, totalZ) > (2 * 10)) {
            // teleported?
            this.removePlayer(player, oldPosition);
            this.addNewPlayer(player, newPosition);
            return;
        }

        // x axis is width
        // z axis is height
        // right refers to the x axis of where we moved
        // top refers to the z axis of where we moved

        // used for relative positioning
        final int up = 1 | (dz >> (Integer.SIZE - 1)); // 1 if dz >= 0, -1 otherwise
        final int right = 1 | (dx >> (Integer.SIZE - 1)); // 1 if dx >= 0, -1 otherwise

        // The area excluded by overlapping the two view distance squares creates four rectangles:
        // Two on the left, and two on the right. The ones on the left we consider the "removed" section
        // and on the right the "added" section.
        // https://i.imgur.com/MrnOBgI.png is a reference image. Note that the outside border is not actually
        // exclusive to the regions they surround.

        // 4 points of the rectangle
        int maxX; // exclusive
        int minX; // inclusive
        int maxZ; // exclusive
        int minZ; // inclusive

        if (dx != 0) {
            // handle right addition

            maxX = toX + (10 * right) + right; // exclusive
            minX = fromX + (10 * right) + right; // inclusive
            maxZ = fromZ + (10 * up) + up; // exclusive
            minZ = toZ - (10 * up); // inclusive

            for (int currX = minX; currX != maxX; currX += right) {
                for (int currZ = minZ; currZ != maxZ; currZ += up) {
                    this.addPlayerTo(player, currX, currZ);
                }
            }
        }

        if (dz != 0) {
            // handle up addition

            maxX = toX + (10 * right) + right; // exclusive
            minX = toX - (10 * right); // inclusive
            maxZ = toZ + (10 * up) + up; // exclusive
            minZ = fromZ + (10 * up) + up; // inclusive

            for (int currX = minX; currX != maxX; currX += right) {
                for (int currZ = minZ; currZ != maxZ; currZ += up) {
                    this.addPlayerTo(player, currX, currZ);
                }
            }
        }

        if (dx != 0) {
            // handle left removal

            maxX = toX - (10 * right); // exclusive
            minX = fromX - (10 * right); // inclusive
            maxZ = fromZ + (10 * up) + up; // exclusive
            minZ = toZ - (10 * up); // inclusive

            for (int currX = minX; currX != maxX; currX += right) {
                for (int currZ = minZ; currZ != maxZ; currZ += up) {
                    this.removePlayerFrom(player, currX, currZ);
                }
            }
        }

        if (dz != 0) {
            // handle down removal

            maxX = fromX + (10 * right) + right; // exclusive
            minX = fromX - (10 * right); // inclusive
            maxZ = toZ - (10 * up); // exclusive
            minZ = fromZ - (10 * up); // inclusive

            for (int currX = minX; currX != maxX; currX += right) {
                for (int currZ = minZ; currZ != maxZ; currZ += up) {
                    this.removePlayerFrom(player, currX, currZ);
                }
            }
        }
    }

    private void removePlayer(final ServerPlayerEntity player, final ChunkSectionPos position) {
        final int x = position.getX();
        final int z = position.getZ();

        for (int xoff = -10; xoff <= 10; ++xoff) {
            for (int zoff = -10; zoff <= 10; ++zoff) {
                this.removePlayerFrom(player, x + xoff, z + zoff);
            }
        }
    }

    private void addNewPlayer(final ServerPlayerEntity player, final ChunkSectionPos position) {
        final int x = position.getX();
        final int z = position.getZ();

        for (int xoff = -10; xoff <= 10; ++xoff) {
            for (int zoff = -10; zoff <= 10; ++zoff) {
                this.addPlayerTo(player, x + xoff, z + zoff);
            }
        }
    }
}