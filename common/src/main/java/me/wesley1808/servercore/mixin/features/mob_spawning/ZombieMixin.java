package me.wesley1808.servercore.mixin.features.mob_spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {
    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
            )
    )
    private boolean servercore$enforceMobCap(boolean doMobSpawning, @Local(ordinal = 0) ServerLevel level) {
        return doMobSpawning && Mobcaps.canSpawnForCategory(
                level,
                this.chunkPosition(),
                EntityType.ZOMBIE.getCategory(),
                Config.get().mobSpawning().zombieReinforcements()
        );
    }
}
