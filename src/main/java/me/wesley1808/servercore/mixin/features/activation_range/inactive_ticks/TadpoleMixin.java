package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.common.interfaces.activation_range.InactiveEntity;
import me.wesley1808.servercore.common.utils.ActivationRange;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Tadpole.class)
public abstract class TadpoleMixin extends AbstractFish implements InactiveEntity {
    @Shadow
    private int age;

    private TadpoleMixin(EntityType<? extends AbstractFish> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void setAge(int i);

    @Override
    public void inactiveTick() {
        this.noActionTime++;
        this.setAge(this.age + 1);
        ActivationRange.updateGoalSelectors(this);
    }
}
