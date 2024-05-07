package me.wesley1808.servercore.mixin.features.breeding_cap.tasks;

import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCap;
import me.wesley1808.servercore.common.config.data.breeding_cap.BreedingCapConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {

    @Inject(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/Villager;eatAndDigestFood()V",
                    ordinal = 0
            )
    )
    private void servercore$enforceBreedCap(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci, @Local(ordinal = 1) Villager mate) {
        BreedingCapConfig config = Config.get().breedingCap();
        if (config.enabled() && config.villagers().exceedsLimit(owner)) {
            BreedingCap.resetAge(owner, mate);
            ci.cancel();
        }
    }
}