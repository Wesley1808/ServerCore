package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.common.config.Config;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MinecraftServer.class, priority = 900)
public class MinecraftServerMixin {

    @ModifyConstant(method = "tickServer", constant = @Constant(intValue = 6000), require = 0)
    public int servercore$modifyAutoSaveInterval(int constant) {
        return Config.get().features().autosaveInterval();
    }
}
