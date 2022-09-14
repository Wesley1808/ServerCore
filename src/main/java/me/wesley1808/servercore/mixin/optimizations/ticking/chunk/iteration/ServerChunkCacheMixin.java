package me.wesley1808.servercore.mixin.optimizations.ticking.chunk.iteration;

import me.wesley1808.servercore.common.interfaces.IChunkMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = ServerChunkCache.class, priority = 900)
public abstract class ServerChunkCacheMixin {
    @Shadow
    @Final
    public ChunkMap chunkMap;

    // Avoid unnecessary array allocations.
    @Redirect(
            method = "tickChunks",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Lists;newArrayListWithCapacity(I)Ljava/util/ArrayList;",
                    remap = false
            )
    )
    private ArrayList<?> servercore$noList(int initialArraySize) {
        return null;
    }

    // Replace the list variable with our own.
    @ModifyVariable(
            method = "tickChunks",
            index = 12,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private List<?> servercore$replaceList(List<?> list) {
        return ((IChunkMap) this.chunkMap).getTickingChunks();
    }

    // Don't add chunks to the list.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;",
                    ordinal = 0
            )
    )
    private Iterable<ChunkHolder> servercore$cancelAddTickingChunks(ChunkMap chunkMap) {
        return Collections::emptyIterator;
    }

    // Don't shuffle the chunk list.
    @Redirect(
            method = "tickChunks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"
            )
    )
    private void servercore$cancelShuffle(List<?> list) {
        // NO-OP
    }
}