package me.wesley1808.servercore.mixin.optimizations.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MapItemSavedData.class, priority = 900)
public class MapItemSavedDataMixin {

    // Cancels unnecessary inventory iteration from maps in item frames to improve performance.
    @Redirect(
            method = "tickCarriedBy",
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean servercore$reduceInventoryIteration(Inventory inventory, ItemStack stack) {
        return stack.isFramed() || inventory.contains(stack);
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
        if (component instanceof TextComponent textComponent) {
            return textComponent.getText();
        }

        return component.getString();
    }
}