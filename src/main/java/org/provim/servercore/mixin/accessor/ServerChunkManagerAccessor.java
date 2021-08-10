package org.provim.servercore.mixin.accessor;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerAccessor {

    @Accessor("ticketManager")
    ChunkTicketManager getTicketManager();

    @Invoker("getChunkHolder")
    ChunkHolder getHolder(long pos);
}
