package org.provim.servercore.mixin.accessor;

import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface TACSAccessor {

    @Accessor("playerChunkWatchingManager")
    PlayerChunkWatchingManager getPlayerChunkWatchingManager();

    @Accessor("world")
    ServerWorld getWorld();
}
