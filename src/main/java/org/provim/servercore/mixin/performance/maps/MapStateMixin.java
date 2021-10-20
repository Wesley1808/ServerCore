package org.provim.servercore.mixin.performance.maps;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MapState.class, priority = 999)
public abstract class MapStateMixin {
    @Shadow
    protected abstract void removeIcon(String id);

    /**
     * Cancels inventory iteration from Maps in Item Frames to save performance.
     *
     * @param playerInventory: The inventory from a PlayerEntity
     * @param stack:           The ItemStack its looking for
     * @return Boolean: whether moving player icons should be tracked by item frames.
     */

    @Redirect(method = "update", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;contains(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean stopInvIteration(PlayerInventory playerInventory, ItemStack stack) {
        return !stack.isInFrame() && playerInventory.contains(stack);
    }

    // Fixes potential bugs with blinking (moving) player icons on player held maps.
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;removeIcon(Ljava/lang/String;)V", ordinal = 0))
    private void removeIcon(MapState mapState, String id, PlayerEntity player, ItemStack stack) {
        if (!stack.isInFrame()) {
            this.removeIcon(id);
        }
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