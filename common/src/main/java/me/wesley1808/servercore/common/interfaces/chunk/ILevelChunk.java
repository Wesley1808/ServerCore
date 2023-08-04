package me.wesley1808.servercore.common.interfaces.chunk;

import net.minecraft.util.RandomSource;

public interface ILevelChunk {
    int servercore$shouldDoLightning(RandomSource randomSource, int thunderChance);
}
