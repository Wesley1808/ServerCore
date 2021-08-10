package org.provim.servercore.mixin.accessor;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface TACSAccessor {

    @Accessor("chunkHolders")
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunkHolders();

    @Invoker("isTooFarFromPlayersToSpawnMobs")
    boolean tooFarFromPlayersToSpawnMobs(ChunkPos chunkPos);
}
