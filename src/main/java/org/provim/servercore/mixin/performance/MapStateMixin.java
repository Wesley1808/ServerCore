package org.provim.servercore.mixin.performance;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.provim.servercore.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapState.class)
public class MapStateMixin {

    /**
     * Cancels inventory iteration from Maps in Item Frames to save performance.
     * Note that this has extremely specific edge cases in which it has the potential
     * to cause subtle bugs regarding moving player icons on player held maps.
     *
     * @param playerInventory: The inventory from a PlayerEntity
     * @param stack:           The ItemStack its looking for
     * @return Boolean: whether or not player icons should be tracked by item frames.
     */

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;contains(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean stopInvIteration(PlayerInventory playerInventory, ItemStack stack) {
        if (Config.instance().stopFrameInventoryIteration) {
            return !stack.isInFrame() && playerInventory.contains(stack);
        }

        return playerInventory.contains(stack);
    }

    /**
     * Replaces getString() in map updates with a faster alternative.
     *
     * @param text: The player name in Text format
     * @return String: The ID string of the icon.
     */

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private String getString(Text text) {
        return ((LiteralText) text).getRawString();
    }
}