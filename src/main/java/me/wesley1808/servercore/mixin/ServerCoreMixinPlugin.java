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
        // Very Many Players
        if (mixinClassName.startsWith(this.mixinPackage + "optimizations.mob_spawning.distance_map")) {
            return !this.isModLoaded("vmp");
        }

        // Lithium
        if (mixinClassName.equals(this.mixinPackage + "features.misc.PortalForcerMixin")) {
            return !this.isModLoaded("lithium");
        }

        // Better Nether Map
        if (mixinClassName.equals(this.mixinPackage + "optimizations.sync_loads.MapItemMixin")) {
            return !this.isModLoaded("nethermap");
        }

        // Cyclonite - Disabled activation range as it attempts to multithread entities.
        if (mixinClassName.startsWith(this.mixinPackage + "features.activation_range")) {
            return !this.isModLoaded("c3h6n6o6");
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