package me.wesley1808.servercore.mixin.features.activation_range.inactive_ticks;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal {
    @Shadow
    public int eggTime;

    @Shadow
    public boolean isChickenJockey;

    private ChickenMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void inactiveTick() {
        super.inactiveTick();

        if (!this.isChickenJockey && this.age >= 0 && this.isAlive() && --this.eggTime <= 0) {
            // VanillaCopy - Spawn chicken egg
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Items.EGG);
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
    }
}