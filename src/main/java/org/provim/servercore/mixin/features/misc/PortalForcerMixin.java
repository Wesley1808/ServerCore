package org.provim.servercore.mixin.features.misc;

import net.minecraft.world.level.portal.PortalForcer;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * From: PaperMC (Add-configurable-portal-search-radius.patch.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PortalForcer.class)
public class PortalForcerMixin {

    @ModifyArg(method = "findPortalAround", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;ensureLoadedAndValid(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;I)V"))
    private int modifyDistance$1(int i) {
        return this.calculateDistance(i == 16);
    }

    @ModifyArg(method = "findPortalAround", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;getInSquare(Ljava/util/function/Predicate;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/entity/ai/village/poi/PoiManager$Occupancy;)Ljava/util/stream/Stream;"))
    private int modifyDistance$2(int i) {
        return this.calculateDistance(i == 16);
    }

    @ModifyArg(method = "createPortal", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"))
    private int modifyDistance$3(int i) {
        return FeatureConfig.PORTAL_CREATE_RADIUS.get();
    }

    private int calculateDistance(boolean isNether) {
        final int range = FeatureConfig.PORTAL_SEARCH_RADIUS.get();
        if (isNether && FeatureConfig.PORTAL_SEARCH_VANILLA_SCALING.get()) {
            return range / 8;
        } else {
            return range;
        }
    }
}
