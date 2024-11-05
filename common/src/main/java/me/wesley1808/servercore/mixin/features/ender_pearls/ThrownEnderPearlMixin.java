package me.wesley1808.servercore.mixin.features.ender_pearls;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.wesley1808.servercore.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
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

    @WrapWithCondition(
            method = "teleport",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;placePortalTicket(Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private boolean servercore$preventEnderpearlChunkLoading(Entity entity, BlockPos blockPos) {
        return !Config.get().features().preventEnderpearlChunkLoading();
    }
}
