package me.wesley1808.servercore.mixin.features.ender_pearls;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.servercore.common.config.Config;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderPearlMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/ThrownEnderpearl;isAlive()Z",
                    ordinal = 0
            )
    )
    private boolean servercore$preventEnderpearlChunkLoading(boolean isEnderpearlAlive) {
        return isEnderpearlAlive && !Config.get().features().preventEnderpearlChunkLoading();
    }
}
