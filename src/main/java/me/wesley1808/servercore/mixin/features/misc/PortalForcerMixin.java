package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * From: PaperMC (Add-configurable-portal-search-radius.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PortalForcer.class)
public class PortalForcerMixin {

    @ModifyConstant(method = "findPortalAround", constant = @Constant(intValue = 128), require = 0)
    private int servercore$modifySearchRadiusOverworld(int constant) {
        return this.calculateDistance(false);
    }

    @ModifyConstant(method = "findPortalAround", constant = @Constant(intValue = 16), require = 0)
    private int servercore$modifySearchRadiusNether(int constant) {
        return this.calculateDistance(true);
    }

    @ModifyConstant(method = "createPortal", constant = @Constant(intValue = 16), require = 0)
    private int servercore$modifyCreateRadius(int constant) {
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
