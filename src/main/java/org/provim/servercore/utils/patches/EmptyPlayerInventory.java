package org.provim.servercore.utils.patches;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

/**
 * An empty instance of PlayerInventory.
 * This is only used to remove unnecessary checks to contains().
 */

public class EmptyPlayerInventory extends PlayerInventory {
    private static EmptyPlayerInventory instance;

    private EmptyPlayerInventory(PlayerEntity player) {
        super(player);
    }

    public static EmptyPlayerInventory getOrCreate(PlayerEntity player) {
        if (instance == null) {
            instance = new EmptyPlayerInventory(player);
        }

        return instance;
    }

    @Override
    public boolean contains(ItemStack stack) {
        return false;
    }
}
