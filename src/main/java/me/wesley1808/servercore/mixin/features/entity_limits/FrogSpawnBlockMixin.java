package me.wesley1808.servercore.mixin.features.entity_limits;

import me.wesley1808.servercore.common.config.tables.EntityLimitConfig;
import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.FrogspawnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FrogspawnBlock.class)
public class FrogSpawnBlockMixin {
    @Unique
    private static final List<EntityType<?>> ENTITIES_TO_CHECK = List.of(EntityType.TADPOLE, EntityType.FROG);

    @Inject(
            method = "hatchFrogspawn",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
            )
    )
    private void servercore$cancelFrogHatching(ServerLevel level, BlockPos pos, RandomSource randomSource, CallbackInfo ci) {
        if (BreedingCap.exceedsLimit(ENTITIES_TO_CHECK, level, pos, EntityLimitConfig.ANIMAL_COUNT.get(), EntityLimitConfig.ANIMAL_RANGE.get())) {
            ci.cancel();
        }
    }
}
