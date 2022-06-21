package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.common.interfaces.activation_range.InactiveEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SkeletonHorse.class)
public abstract class SkeletonHorseMixin extends AbstractHorse implements InactiveEntity {
    @Shadow
    @Final
    private static int TRAP_MAX_LIFE;

    @Shadow
    private boolean isTrap;

    @Shadow
    private int trapTime;

    private SkeletonHorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        if (this.isTrap && this.trapTime++ >= TRAP_MAX_LIFE) {
            this.discard();
        }
    }
}
