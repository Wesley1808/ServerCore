package me.wesley1808.servercore.mixin.validation;

import me.wesley1808.servercore.common.config.tables.ItemValidationConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * From: PaperMC (Book-Size-Limits.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerEditBookMixin {
    @Shadow
    @Final
    static Logger LOGGER;

    @Shadow
    public ServerPlayer player;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract void disconnect(Component component);

    @Inject(method = "handleEditBook", at = @At(value = "HEAD"), cancellable = true)
    private void servercore$handleEditBook(ServerboundEditBookPacket packet, CallbackInfo ci) {
        if (this.server.isSameThread()) {
            return;
        }

        List<String> pageList = packet.getPages();
        long byteTotal = 0;
        int maxBookPageSize = ItemValidationConfig.MAX_BOOK_PAGE_SIZE.get();
        double multiplier = Math.max(0.3D, Math.min(1D, ItemValidationConfig.BOOK_TOTAL_MULTIPLIER.get()));
        long byteAllowed = maxBookPageSize;

        for (String testString : pageList) {
            int byteLength = testString.getBytes(StandardCharsets.UTF_8).length;
            if (byteLength > 256 * 4) {
                LOGGER.warn(this.player.getScoreboardName() + " tried to send a book with with a page too large!");
                this.disconnect(Component.literal("Book too large!"));
                ci.cancel();
                return;
            }

            byteTotal += byteLength;
            int length = testString.length();
            int multibytes = 0;
            if (byteLength != length) {
                for (char c : testString.toCharArray()) {
                    if (c > 127) {
                        multibytes++;
                    }
                }
            }

            byteAllowed += (maxBookPageSize * Math.min(1, Math.max(0.1D, (double) length / 255D))) * multiplier;

            if (multibytes > 1) {
                // penalize MB
                byteAllowed -= multibytes;
            }
        }

        if (byteTotal > byteAllowed) {
            LOGGER.warn("{} tried to send too large of a book. Book Size: {} - Allowed:  {} - Pages: {}", this.player.getScoreboardName(), byteTotal, byteAllowed, pageList.size());
            this.disconnect(Component.literal("Book too large!"));
            ci.cancel();
        }
    }
}
