package me.wesley1808.servercore.mixin;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.tables.OptimizationConfig;
import me.wesley1808.servercore.common.services.Environment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ServerCoreMixinPlugin implements IMixinConfigPlugin {
    private String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage + ".";
        Config.load(false);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String path = mixinClassName.substring(this.mixinPackage.length());

        if (path.startsWith("optimizations.sync_loads")) {
            return OptimizationConfig.REDUCE_SYNC_LOADS.get();
        }

        if (path.startsWith("optimizations.biome_lookups")) {
            return OptimizationConfig.FAST_BIOME_LOOKUPS.get();
        }

        if (path.startsWith("optimizations.ticking.chunk.cache")) {
            return OptimizationConfig.CACHE_TICKING_CHUNKS.get();
        }

        // Immersive Portals
        if (path.equalsIgnoreCase("features.misc.ChunkMapMixin")) {
            return !Environment.INSTANCE.isModLoaded("immersive_portals");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}