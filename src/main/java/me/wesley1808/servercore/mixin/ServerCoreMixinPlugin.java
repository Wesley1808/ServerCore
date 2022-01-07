package me.wesley1808.servercore.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ServerCoreMixinPlugin implements IMixinConfigPlugin {
    private static final String DEFAULT_PATH = "me.wesley1808.servercore.mixin.";

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override // Disables specific mixins for mod compatibility.
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        // Very Many Players
        if (mixinClassName.startsWith(DEFAULT_PATH + "optimizations.mob_spawning.distance_map")) {
            return !FabricLoader.getInstance().isModLoaded("vmp");
        }

        // The Aether Reborn & Better Nether Map
        if (mixinClassName.equals(DEFAULT_PATH + "optimizations.chunk_loading.MapItemMixin")) {
            return !(FabricLoader.getInstance().isModLoaded("the_aether") || FabricLoader.getInstance().isModLoaded("nethermap"));
        }

        // Essentials Commands
        if (mixinClassName.equals(DEFAULT_PATH + "optimizations.chunk_loading.BlockGetterMixin")) {
            return !FabricLoader.getInstance().isModLoaded("essential_commands");
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