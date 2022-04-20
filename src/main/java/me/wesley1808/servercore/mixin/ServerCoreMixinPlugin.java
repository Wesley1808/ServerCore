package me.wesley1808.servercore.mixin;

import me.wesley1808.servercore.config.Config;
import me.wesley1808.servercore.config.tables.FeatureConfig;
import net.fabricmc.loader.api.FabricLoader;
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
        Config.load();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override // Disables specific mixins for mod compatibility.
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // C2ME - Disabled (small) chunk ticking optimizations due to these mixins accessing the world's random at chunk initialization.
        // Since C2ME can run this asynchronously, it will crash the server because the random is being accessed from a different thread.
        // This happens because Mojang decided crashing the server is a better solution than ensuring thread safety.
        if (mixinClassName.startsWith(this.mixinPackage + "optimizations.ticking.chunk")) {
            return !this.isModLoaded("c2me");
        }

        // Lithium - Disabled configurable portal radius in favor of portal / POI optimizations.
        if (mixinClassName.equals(this.mixinPackage + "features.misc.PortalForcerMixin")) {
            return !this.isModLoaded("lithium");
        }

        // Very Many Players - Disabled distance maps for mobspawning, as VMP implements its own distance maps.
        if (mixinClassName.startsWith(this.mixinPackage + "optimizations.mob_spawning.distance_map")) {
            return !this.isModLoaded("vmp");
        }

        // Disable spawn chunk mixins if the setting is set to false - to minimize mod conflicts.
        if (mixinClassName.startsWith(this.mixinPackage + "features.spawn_chunks")) {
            return FeatureConfig.DISABLE_SPAWN_CHUNKS.get();
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

    private boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    private boolean isModLoaded(String... ids) {
        for (String id : ids) {
            if (this.isModLoaded(id)) {
                return true;
            }
        }
        return false;
    }
}