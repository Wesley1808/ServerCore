package org.provim.servercore.mixin.accessor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnHelper.class)
public interface SpawnHelperAccessor {

    @Accessor("CHUNK_AREA")
    static int getChunkArea() {
        throw new AssertionError();
    }

    @Invoker("getBiomeDirectly")
    static Biome getBiome(BlockPos pos, Chunk chunk) {
        throw new AssertionError();
    }
}