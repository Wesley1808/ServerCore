package me.wesley1808.servercore.mixin.features.mob_spawning;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/effect/OozingMobEffect")
public class OozingMobEffectMixin {

    @Inject(
            method = "onMobRemoved",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/ToIntFunction;applyAsInt(Ljava/lang/Object;)I",
                    ordinal = 0
            )
    )
    private void servercore$enforceMobcap(LivingEntity entity, int amplifier, Entity.RemovalReason reason, CallbackInfo ci) {
        boolean canSpawn = Mobcaps.canSpawnForCategory(
                entity.level(),
                entity.chunkPosition(),
                EntityType.SLIME.getCategory(),
                Config.get().mobSpawning().potionEffects()
        );

        if (!canSpawn) {
            ci.cancel();
        }
    }
}
