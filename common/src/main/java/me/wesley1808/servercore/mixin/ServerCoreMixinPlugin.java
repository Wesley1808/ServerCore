package me.wesley1808.servercore.mixin;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.OptimizationConfig;
import me.wesley1808.servercore.common.utils.Environment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ServerCoreMixinPlugin implements IMixinConfigPlugin {
    private String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage + ".";
        Config.loadOptimizationConfig();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String path = mixinClassName.substring(this.mixinPackage.length());
        OptimizationConfig config = Config.optimizations();
        boolean shouldApply = true;

        if (path.startsWith("optimizations.sync_loads")) {
            shouldApply &= config.reduceSyncLoads();
        }

        if (path.startsWith("optimizations.biome_lookups")) {
            shouldApply &= config.fastBiomeLookups();
        }

        if (path.startsWith("optimizations.command_blocks")) {
            shouldApply &= config.optimizeCommandBlocks();
        }

        if (path.startsWith("optimizations.ticking.chunk.cache")) {
            shouldApply &= config.cacheTickingChunks();
        }

        if (path.equals("optimizations.ticking.chunk.random.LiquidBlockMixin")) {
            shouldApply &= config.cancelDuplicateFluidTicks();
        }

        if (path.startsWith("optimizations.ticking.chunk")) {
            shouldApply &= !Environment.MOD_MOONRISE;
        }

        return shouldApply;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}