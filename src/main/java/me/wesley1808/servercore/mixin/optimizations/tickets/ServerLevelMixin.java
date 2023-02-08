package me.wesley1808.servercore.mixin.optimizations.tickets;

import me.wesley1808.servercore.common.utils.ChunkManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    private ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Override
    public int getHeight(@NotNull Heightmap.Types types, int blockX, int blockZ) {
        if (blockX >= -30000000 && blockZ >= -30000000 && blockX < 30000000 && blockZ < 30000000) {
            ChunkAccess chunk = ChunkManager.getChunkNow(this, blockX >> 4, blockZ >> 4);
            return chunk == null ? this.getMinBuildHeight() : chunk.getHeight(types, blockX & 15, blockZ & 15) + 1;
        } else {
            return this.getSeaLevel() + 1;
        }
    }
}
