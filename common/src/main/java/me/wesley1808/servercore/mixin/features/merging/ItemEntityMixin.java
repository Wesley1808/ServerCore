package me.wesley1808.servercore.mixin.features.merging;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @ModifyConstant(method = "mergeWithNeighbours", require = 0, constant = @Constant(doubleValue = 0.5))
    private double servercore$modifyMergeRadius(double constant) {
        return FeatureConfig.ITEM_MERGE_RADIUS.get();
    }
}