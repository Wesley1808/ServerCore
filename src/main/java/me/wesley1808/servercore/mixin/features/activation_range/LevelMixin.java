package me.wesley1808.servercore.mixin.features.activation_range;

import me.wesley1808.servercore.common.interfaces.activation_range.LevelInfo;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Based on: Paper (Entity-Activation-Range-2.0.patch)
 * Patch Author: Aikar (aikar@aikar.co)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(Level.class)
public abstract class LevelMixin implements LevelInfo {
    @Unique
    private int remainingAnimals;

    @Unique
    private int remainingFlying;

    @Unique
    private int remainingMonsters;

    @Unique
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
