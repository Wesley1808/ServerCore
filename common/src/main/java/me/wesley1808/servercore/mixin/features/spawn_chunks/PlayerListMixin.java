package me.wesley1808.servercore.mixin.features.spawn_chunks;

import me.wesley1808.servercore.common.config.tables.FeatureConfig;
import me.wesley1808.servercore.common.utils.ChunkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;invalidateStatus()V"))
    private void servercore$disableSpawnChunks(Connection connection, ServerPlayer player, CallbackInfo ci) {
        // Disable spawn chunks after the player joins.
        // This is only used on singleplayer worlds that aren't published.
        if (!player.server.isPublished() && FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            ChunkManager.disableSpawnChunks(player.server);
        }
    }
}
