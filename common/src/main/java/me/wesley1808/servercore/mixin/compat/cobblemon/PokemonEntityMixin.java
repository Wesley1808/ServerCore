package me.wesley1808.servercore.mixin.compat.cobblemon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PokemonEntity.class, remap = false)
public abstract class PokemonEntityMixin extends ShoulderRidingEntity {
    @Shadow
    private boolean queuedToDespawn;
    @Shadow
    private int ticksLived;

    private PokemonEntityMixin(EntityType<? extends ShoulderRidingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void servercore$inactiveTick() {
        super.servercore$inactiveTick();

        if (this.queuedToDespawn) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        this.ticksLived++;
    }
}
