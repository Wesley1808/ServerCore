package org.provim.servercore.utils.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.provim.servercore.interfaces.IServerPlayer;

import java.util.List;

/**
 * From: PaperMC (implement-optional-per-player-mob-spawns.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 *
 * @author Spottedleaf
 */

public final class PlayerMobDistanceMap {

    private static final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> EMPTY_SET = new PooledHashSets.PooledObjectLinkedOpenHashSet<>();

    private final Object2ObjectOpenHashMap<ServerPlayer, SectionPos> players = new Object2ObjectOpenHashMap<>();
    // we use linked for better iteration.
    private final Long2ObjectOpenHashMap<PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer>> playerMap = new Long2ObjectOpenHashMap<>(32, 0.5f);

    private final PooledHashSets<ServerPlayer> pooledHashSets = new PooledHashSets<>();
    private int viewDistance;

    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getPlayersInRange(final ChunkPos chunkPos) {
        return this.getPlayersInRange(chunkPos.x, chunkPos.z);
    }

    public PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> getPlayersInRange(final int chunkX, final int chunkZ) {
        return this.playerMap.getOrDefault(ChunkPos.asLong(chunkX, chunkZ), EMPTY_SET);
    }

    public void update(final List<ServerPlayer> currentPlayers, final int newViewDistance) {
        final ObjectLinkedOpenHashSet<ServerPlayer> gone = new ObjectLinkedOpenHashSet<>(this.players.keySet());

        final int oldViewDistance = this.viewDistance;
        this.viewDistance = newViewDistance;

        for (final ServerPlayer player : currentPlayers) {
            if (!player.isSpectator()) {
                gone.remove(player);

                final SectionPos newPosition = player.getLastSectionPos();
                final SectionPos oldPosition = this.players.put(player, newPosition);

                if (oldPosition == null) {
                    this.addNewPlayer(player, newPosition, newViewDistance);
                } else {
                    this.updatePlayer(player, oldPosition, newPosition, oldViewDistance, newViewDistance);
                }
            }
        }

        for (final ServerPlayer player : gone) {
            final SectionPos oldPosition = this.players.remove(player);
            if (oldPosition != null) {
                this.removePlayer(player, oldPosition, oldViewDistance);
            }
        }
    }

    private void addPlayerTo(final ServerPlayer player, final int chunkX, final int chunkZ) {
        this.playerMap.compute(ChunkPos.asLong(chunkX, chunkZ), (final Long key, final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> players) -> {
            if (players == null) {
                return ((IServerPlayer) player).getCachedSingleMobDistanceMap();
            } else {
                return PlayerMobDistanceMap.this.pooledHashSets.findMapWith(players, player);
            }
        });
    }

    private void removePlayerFrom(final ServerPlayer player, final int chunkX, final int chunkZ) {
        this.playerMap.compute(ChunkPos.asLong(chunkX, chunkZ), (final Long keyInMap, final PooledHashSets.PooledObjectLinkedOpenHashSet<ServerPlayer> players) -> {
            if (players == null) {
                return ((IServerPlayer) player).getCachedSingleMobDistanceMap();
            } else {
                return PlayerMobDistanceMap.this.pooledHashSets.findMapWithout(players, player);
            }
        });
    }

    private void updatePlayer(final ServerPlayer player, final SectionPos oldPosition, final SectionPos newPosition, final int oldViewDistance, final int newViewDistance) {
        final int toX = newPosition.getX();
        final int toZ = newPosition.getZ();
        final int fromX = oldPosition.getX();
        final int fromZ = oldPosition.getZ();

        final int dx = toX - fromX;
        final int dz = toZ - fromZ;

        final int totalX = Math.abs(fromX - toX);
        final int totalZ = Math.abs(fromZ - toZ);

        if (Math.max(totalX, totalZ) > (2 * oldViewDistance)) {
            // teleported?
            this.removePlayer(player, oldPosition, oldViewDistance);
            this.addNewPlayer(player, newPosition, newViewDistance);
            return;
        }

        // x axis is width
        // z axis is height
        // right refers to the x axis of where we moved
        // top refers to the z axis of where we moved

        if (oldViewDistance == newViewDistance) {
            // same view distance

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

                maxX = toX + (oldViewDistance * right) + right; // exclusive
                minX = fromX + (oldViewDistance * right) + right; // inclusive
                maxZ = fromZ + (oldViewDistance * up) + up; // exclusive
                minZ = toZ - (oldViewDistance * up); // inclusive

                for (int currX = minX; currX != maxX; currX += right) {
                    for (int currZ = minZ; currZ != maxZ; currZ += up) {
                        this.addPlayerTo(player, currX, currZ);
                    }
                }
            }

            if (dz != 0) {
                // handle up addition

                maxX = toX + (oldViewDistance * right) + right; // exclusive
                minX = toX - (oldViewDistance * right); // inclusive
                maxZ = toZ + (oldViewDistance * up) + up; // exclusive
                minZ = fromZ + (oldViewDistance * up) + up; // inclusive

                for (int currX = minX; currX != maxX; currX += right) {
                    for (int currZ = minZ; currZ != maxZ; currZ += up) {
                        this.addPlayerTo(player, currX, currZ);
                    }
                }
            }

            if (dx != 0) {
                // handle left removal

                maxX = toX - (oldViewDistance * right); // exclusive
                minX = fromX - (oldViewDistance * right); // inclusive
                maxZ = fromZ + (oldViewDistance * up) + up; // exclusive
                minZ = toZ - (oldViewDistance * up); // inclusive

                for (int currX = minX; currX != maxX; currX += right) {
                    for (int currZ = minZ; currZ != maxZ; currZ += up) {
                        this.removePlayerFrom(player, currX, currZ);
                    }
                }
            }

            if (dz != 0) {
                // handle down removal

                maxX = fromX + (oldViewDistance * right) + right; // exclusive
                minX = fromX - (oldViewDistance * right); // inclusive
                maxZ = toZ - (oldViewDistance * up); // exclusive
                minZ = fromZ - (oldViewDistance * up); // inclusive

                for (int currX = minX; currX != maxX; currX += right) {
                    for (int currZ = minZ; currZ != maxZ; currZ += up) {
                        this.removePlayerFrom(player, currX, currZ);
                    }
                }
            }
        } else {
            // different view distance
            // for now :)
            this.removePlayer(player, oldPosition, oldViewDistance);
            this.addNewPlayer(player, newPosition, newViewDistance);
        }
    }

    private void removePlayer(final ServerPlayer player, final SectionPos position, final int viewDistance) {
        final int x = position.getX();
        final int z = position.getZ();

        for (int xoff = -viewDistance; xoff <= viewDistance; ++xoff) {
            for (int zoff = -viewDistance; zoff <= viewDistance; ++zoff) {
                this.removePlayerFrom(player, x + xoff, z + zoff);
            }
        }
    }

    private void addNewPlayer(final ServerPlayer player, final SectionPos position, final int viewDistance) {
        final int x = position.getX();
        final int z = position.getZ();

        for (int xoff = -viewDistance; xoff <= viewDistance; ++xoff) {
            for (int zoff = -viewDistance; zoff <= viewDistance; ++zoff) {
                this.addPlayerTo(player, x + xoff, z + zoff);
            }
        }
    }
}
