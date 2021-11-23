package org.provim.servercore.mixin.features.activation_range;

import net.minecraft.world.level.Level;
import org.provim.servercore.interfaces.activation_range.ILevel;
import org.spongepowered.asm.mixin.Mixin;

/**
 * From: PaperMC (Entity-Activation-Range-2.0.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Level.class)
public abstract class LevelMixin implements ILevel {
    private int remainingAnimals;
    private int remainingFlying;
    private int remainingMonsters;
    private int remainingVillagers;

    @Override
    public int getRemainingVillagers() {
        return this.remainingVillagers;
    }

    @Override
    public void setRemainingVillagers(int count) {
        this.remainingVillagers = count;
    }

    @Override
    public int getRemainingMonsters() {
        return this.remainingMonsters;
    }

    @Override
    public void setRemainingMonsters(int count) {
        this.remainingMonsters = count;
    }

    @Override
    public int getRemainingAnimals() {
        return this.remainingAnimals;
    }

    @Override
    public void setRemainingAnimals(int count) {
        this.remainingAnimals = count;
    }

    @Override
    public int getRemainingFlying() {
        return this.remainingFlying;
    }

    @Override
    public void setRemainingFlying(int count) {
        this.remainingFlying = count;
    }
}
