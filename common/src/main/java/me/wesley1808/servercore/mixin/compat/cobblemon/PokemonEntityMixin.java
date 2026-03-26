package me.wesley1808.servercore.mixin.compat.cobblemon;

/* TODO
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
*/