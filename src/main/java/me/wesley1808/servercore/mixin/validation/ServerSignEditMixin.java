package me.wesley1808.servercore.mixin.validation;

import me.wesley1808.servercore.common.config.tables.ItemValidationConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * From: PaperMC (Limit-Client-Sign-length-more.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerSignEditMixin {
    @Shadow protected abstract CompletableFuture<List<FilteredText>> filterTextPacket(List<String> list);

    @Shadow protected abstract void updateSignText(ServerboundSignUpdatePacket serverboundSignUpdatePacket, List<FilteredText> list);

    @Shadow @Final private MinecraftServer server;

    /**
     * @author Celeste Peláez
     * @reason Prevent signs from being too long
     */
    @Overwrite
    public void handleSignUpdate(ServerboundSignUpdatePacket packet) {
        int maxSignLineLength = ItemValidationConfig.MAX_SIGN_LINE_LENGTH.get();
        String[] lines = packet.getLines();

        for (int i = 0; i < lines.length; ++i) {
            if (maxSignLineLength > 0 && lines[i].length() > maxSignLineLength) {
                // This handles multibyte characters as 1
                int offset = lines[i].codePoints().limit(maxSignLineLength).map(Character::charCount).sum();
                if (offset < lines[i].length()) {
                    lines[i] = lines[i].substring(0, offset); // this will break any filtering, but filtering is NYI as of 1.17
                }
            }
        }

        List<String> list = Stream.of(lines).map(ChatFormatting::stripFormatting).toList();
        this.filterTextPacket(list).thenAcceptAsync(updatedList -> this.updateSignText(packet, updatedList), this.server);
    }
}
