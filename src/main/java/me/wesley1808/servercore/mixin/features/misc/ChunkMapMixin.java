package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

/**
 * Based on: Paper (Workaround-for-client-lag-spikes-MC-162253.patch)
 * <p>
 * When crossing certain chunk boundaries, the client needlessly
 * calculates light maps for chunk neighbours. In some specific map
 * configurations, these calculations cause a 500ms+ freeze on the Client.
 * <p>
 * This patch basically serves as a workaround by sending light maps
 * to the client, so that it doesn't attempt to calculate them.
 * This mitigates the frametime impact to a minimum (but it's still there).
 * <p>
 * Patch Author: Brokkonaut (hannos17@gmx.de)
 * <br>
 * Original Patch Author: MeFisto94 (MeFisto94@users.noreply.github.com)
 * <br>
 * Co-authored By: Daniel Goossens (daniel@goossens.ch)
 * <br>
 * Co-authored By: Nassim Jahnke (nassim@njahnke.dev)
 */
@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Shadow
    @Final
    private ThreadedLevelLightEngine lightEngine;
    @Shadow
    @Final
    public ServerLevel level;
    @Shadow
    int viewDistance;

    @Inject(
            method = "playerLoadedChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/mutable/MutableObject;setValue(Ljava/lang/Object;)V",
                    ordinal = 0

            )
    )
    private void servercore$preventClientLag(ServerPlayer player, MutableObject<ClientboundLevelChunkWithLightPacket> packet, LevelChunk chunk, CallbackInfo ci) {
        if (!FeatureConfig.FIX_CLIENT_LAG_ON_CHUNKBORDERS.get()) {
            return;
        }

        final int chunkX = chunk.getPos().x;
        final int chunkZ = chunk.getPos().z;
        final int playerChunkX = player.chunkPosition().x;
        final int playerChunkZ = player.chunkPosition().z;

        // For all loaded neighbours, send skylight for empty sections above highest non-empty section (+1) of the center chunk
        // otherwise the client will try to calculate lighting there on its own
        final BitSet lightMask = this.lightMask(chunk);
        if (!lightMask.isEmpty()) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    final int neighborChunkX = chunkX + x;
                    final int neighborChunkZ = chunkZ + z;
                    final int distX = Math.abs(playerChunkX - neighborChunkX);
                    final int distZ = Math.abs(playerChunkZ - neighborChunkZ);
                    if (Math.max(distX, distZ) > this.viewDistance - 1) {
                        continue;
                    }

                    final ChunkAccess neighbor = ChunkManager.getChunkNow(this.level, neighborChunkX, neighborChunkZ);
                    if (neighbor == null) {
                        continue;
                    }

                    final BitSet updateLightMask = (BitSet) lightMask.clone();
                    updateLightMask.andNot(this.ceilingLightMask(neighbor));
                    if (updateLightMask.isEmpty()) {
                        continue;
                    }

                    player.connection.send(new ClientboundLightUpdatePacket(new ChunkPos(neighborChunkX, neighborChunkZ), this.lightEngine, updateLightMask, null, true));
                }
            }
        }
    }

    /**
     * Returns the light mask for the given chunk consisting of all non-empty sections that may need sending.
     */
    @Unique
    private BitSet lightMask(final ChunkAccess chunk) {
        final LevelChunkSection[] sections = chunk.getSections();
        final BitSet mask = new BitSet(this.lightEngine.getLightSectionCount());

        // There are 2 more light sections than chunk sections so when iterating over
        // sections we have to increment the index by 1
        for (int i = 0; i < sections.length; i++) {
            if (!sections[i].hasOnlyAir()) {
                // Whenever a section is not empty, it can change lighting for the section itself (i + 1), the section below, and the section above
                mask.set(i);
                mask.set(i + 1);
                mask.set(i + 2);
                i++; // We can skip the already set upper section
            }
        }
        return mask;
    }

    /**
     * Returns the ceiling light mask of all sections that are equal or lower to the highest non-empty section.
     */
    @Unique
    private BitSet ceilingLightMask(final ChunkAccess chunk) {
        final LevelChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].hasOnlyAir()) {
                // Add one to get the light section, one because blocks in the section above may change, and another because BitSet's toIndex is exclusive
                final int highest = i + 3;
                final BitSet mask = new BitSet(highest);
                mask.set(0, highest);
                return mask;
            }
        }
        return new BitSet();
    }
}
