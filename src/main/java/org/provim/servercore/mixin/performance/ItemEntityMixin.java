package org.provim.servercore.mixin.performance;

import net.minecraft.entity.ItemEntity;
import org.provim.servercore.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    /**
     * Merges item entities from a set range.
     *
     * @param args: The values that decide the merging range.
     */

    @ModifyArgs(method = "tryMerge()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private void setMergeRadius(Args args) {
        double mergeRadius = Config.getFeatureConfig().itemMergeRadius;
        args.set(0, mergeRadius);
        args.set(2, mergeRadius);
    }
}