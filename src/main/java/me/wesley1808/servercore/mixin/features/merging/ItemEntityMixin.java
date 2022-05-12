package me.wesley1808.servercore.mixin.features.merging;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @ModifyArgs(
            method = "mergeWithNeighbours",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void modifyMergeRadius(Args args) {
        double mergeRadius = FeatureConfig.ITEM_MERGE_RADIUS.get();
        args.set(0, mergeRadius);
        args.set(2, mergeRadius);
    }
}