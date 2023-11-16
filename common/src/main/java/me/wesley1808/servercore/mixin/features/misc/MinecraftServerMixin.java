package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MinecraftServer.class, priority = 900)
public class MinecraftServerMixin {

    @ModifyConstant(method = "computeNextAutosaveInterval", constant = @Constant(floatValue = 300F), require = 0)
    public float servercore$modifyAutoSaveInterval(float constant) {
        return FeatureConfig.AUTOSAVE_INTERVAL_SECONDS.get();
    }
}
