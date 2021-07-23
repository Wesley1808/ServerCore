package org.provim.servercore.mixin.accessor;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface TACSAccessor {

    @Accessor("watchDistance")
    int getWatchDistance();

    @Invoker("isTooFarFromPlayersToSpawnMobs")
    boolean tooFarFromPlayersToSpawnMobs(ChunkPos chunkPos);
}
