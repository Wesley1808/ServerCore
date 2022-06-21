package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import me.wesley1808.servercore.common.interfaces.activation_range.InactiveEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal implements InactiveEntity {
    @Shadow
    public int eggTime;

    @Shadow
    public boolean isChickenJockey;

    private ChickenMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        if (!this.isChickenJockey && this.age >= 0 && this.isAlive()) {
            --this.eggTime;
        }
    }
}