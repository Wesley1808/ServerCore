package org.provim.servercore.mixin.features.merging;

import net.minecraft.world.entity.item.ItemEntity;
import org.provim.servercore.config.tables.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    // Configurable item merging radius.
    @ModifyArgs(method = "mergeWithNeighbours", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"))
    private void setMergeRadius(Args args) {
        double mergeRadius = FeatureConfig.ITEM_MERGE_RADIUS.get();
        args.set(0, mergeRadius);
        args.set(2, mergeRadius);
    }
}