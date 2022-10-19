package me.wesley1808.servercore.mixin.features.breeding_cap.tasks;

import me.wesley1808.servercore.common.utils.BreedingCap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {

    @Inject(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/Villager;eatAndDigestFood()V",
                    ordinal = 0
            )
    )
    private void servercore$enforceBreedCap(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci, Villager mate) {
        if (BreedingCap.VILLAGER.exceedsLimit(owner)) {
            BreedingCap.resetAge(owner, mate);
            ci.cancel();
        }
    }
}