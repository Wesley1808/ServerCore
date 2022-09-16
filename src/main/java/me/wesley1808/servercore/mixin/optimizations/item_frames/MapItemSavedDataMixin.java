package me.wesley1808.servercore.mixin.optimizations.item_frames;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MapItemSavedData.class, priority = 900)
public abstract class MapItemSavedDataMixin {

    @Shadow
    protected abstract void removeDecoration(String string);

    // Cancels unnecessary inventory iteration from maps in item frames to improve performance.
    @Redirect(
            method = "tickCarriedBy",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean servercore$cancelInventoryIteration(Inventory inventory, ItemStack stack) {
        return !stack.isFramed() && inventory.contains(stack);
    }

    // Fixes blinking player icons on player held maps.
    @Redirect(
            method = "tickCarriedBy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;removeDecoration(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    private void servercore$removePlayerIcon(MapItemSavedData data, String id, Player player, ItemStack stack) {
        if (!stack.isFramed()) {
            this.removeDecoration(id);
        }
    }

    @Redirect(
            method = "tickCarriedBy",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"
            )
    )
    private String servercore$getPlayerName(Component component) {
        if (component.getContents() instanceof LiteralContents literal) {
            return literal.text();
        }

        return component.getString();
    }
}