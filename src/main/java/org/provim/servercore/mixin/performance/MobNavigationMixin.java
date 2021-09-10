package org.provim.servercore.mixin.performance;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.provim.servercore.utils.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobNavigation.class)
public class MobNavigationMixin {

    // Don't load chunks for pathfinding.
    @Redirect(method = "findPathTo(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState onlyPathfindIfLoaded(World world, BlockPos pos) {
        return ChunkManager.getStateIfLoaded(world, pos);
    }
}
