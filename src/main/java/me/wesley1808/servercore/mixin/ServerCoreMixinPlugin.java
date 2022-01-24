package me.wesley1808.servercore.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ServerCoreMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_CLASS_PATH = "me.wesley1808.servercore.mixin.";

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
        if (mixinClassName.startsWith(MIXIN_CLASS_PATH + "optimizations.mob_spawning.distance_map")) {
            return !this.isModLoaded("vmp");
        }

        // The Aether Reborn & Better Nether Map
        if (mixinClassName.equals(MIXIN_CLASS_PATH + "optimizations.chunk_loading.MapItemMixin")) {
            return !this.isModLoaded("the_aether", "nethermap");
        }

        // Essentials Commands
        if (mixinClassName.equals(MIXIN_CLASS_PATH + "optimizations.chunk_loading.BlockGetterMixin")) {
            return !this.isModLoaded("essential_commands");
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