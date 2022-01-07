package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.config.tables.FeatureConfig;
import me.wesley1808.servercore.utils.ChunkManager;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.Set;

/**
 * From: PaperMC (Add-option-to-prevent-players-from-moving-into-unloaded-chunks.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    @Final
    public Connection connection;

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void teleport(double d, double e, double f, float g, float h, Set<ClientboundPlayerPositionPacket.RelativeArgument> set, boolean bl);

    @Inject(method = "handleMoveVehicle", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/world/phys/Vec3;lengthSqr()D"), cancellable = true)
    private void handleMoveVehicle(ServerboundMoveVehiclePacket packet, CallbackInfo ci, Entity entity, ServerLevel serverLevel, double d, double e, double f, double g, double h, double i, float j, float k, double l, double m, double n) {
        if (FeatureConfig.PREVENT_MOVING_INTO_UNLOADED_CHUNKS.get() && !ChunkManager.isChunkLoaded(serverLevel, (int) Math.floor(packet.getX()) >> 4, (int) Math.floor(packet.getZ()) >> 4)) {
            this.connection.send(new ClientboundMoveVehiclePacket(entity));
            ci.cancel();
        }
    }

    @Inject(method = "handleMovePlayer", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerPlayer;getBoundingBox()Lnet/minecraft/world/phys/AABB;"), cancellable = true)
    private void handleMovePlayer(ServerboundMovePlayerPacket packet, CallbackInfo ci, ServerLevel serverLevel, double toX, double toY, double toZ, float yRot, float xRot, double fromX, double fromY, double fromZ, double l) {
        if (FeatureConfig.PREVENT_MOVING_INTO_UNLOADED_CHUNKS.get() && (fromX != toX || fromZ != toZ) && ChunkManager.isTouchingUnloadedChunk(serverLevel, new AABB(toX - 4, toY, toZ - 4, toX + 4, toY, toZ + 4))) {
            this.teleport(fromX, fromY, fromZ, yRot, xRot, Collections.emptySet(), true);
            ci.cancel();
        }
    }
}
