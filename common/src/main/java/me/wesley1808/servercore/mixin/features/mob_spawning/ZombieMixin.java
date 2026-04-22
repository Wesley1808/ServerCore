package me.wesley1808.servercore.mixin.features.mob_spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.utils.Mobcaps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {
    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isSpawningMonsters()Z"
            )
    )
    private boolean servercore$enforceMobCap(boolean canSpawnMonsters, ServerLevel level) {
        return canSpawnMonsters && Mobcaps.canSpawnForCategory(
                level,
                this.chunkPosition(),
                EntityTypes.ZOMBIE.getCategory(),
                Config.get().mobSpawning().zombieReinforcements()
        );
    }
}
