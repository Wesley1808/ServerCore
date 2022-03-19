package me.wesley1808.servercore.mixin.features.entity_limits;

import me.wesley1808.servercore.config.tables.EntityLimitConfig;
import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.FrogspawnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FrogspawnBlock.class)
public class FrogSpawnBlockMixin {

    @Inject(
            method = "hatchFrogspawn",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
            )
    )
    private void cancelFrogSpawn(ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
        if (TickManager.checkForEntities(new EntityType[]{EntityType.TADPOLE, EntityType.FROG}, level, pos, EntityLimitConfig.ANIMAL_COUNT.get(), EntityLimitConfig.ANIMAL_RANGE.get())) {
            ci.cancel();
        }
    }
}
