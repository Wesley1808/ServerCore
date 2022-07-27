package me.wesley1808.servercore.mixin.features.activation_range.fixes;

import me.wesley1808.servercore.common.config.tables.ActivationRangeConfig;
import me.wesley1808.servercore.common.config.tables.DynamicBrainActivationConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerPanicTrigger;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * From: Pufferfish (Dynamic-Activation-of-Brain.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(VillagerPanicTrigger.class)
public class VillagerPanicTriggerMixin {
    @Unique
    private long nextGolemPanic = -1;

    @ModifyVariable(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", index = 3, at = @At(value = "HEAD"), argsOnly = true)
    private long servercore$fixGolemSpawning(long value, ServerLevel level, Villager villager, long gameTime) {
        if (ActivationRangeConfig.ENABLED.get() || DynamicBrainActivationConfig.ENABLED.get()) {
            if (this.nextGolemPanic < 0) {
                this.nextGolemPanic = gameTime + 100;
            }

            if (--this.nextGolemPanic < gameTime) {
                this.nextGolemPanic = -1;
                return 100;
            }
        }

        return value;
    }
}
