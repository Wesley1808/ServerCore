package me.wesley1808.servercore.mixin.features.spawn_chunks;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MinecraftServer.class, priority = 900)
public abstract class MinecraftServerMixin {
    @Unique
    private static final boolean KSYXIS_LOADED = FabricLoader.getInstance().isModLoaded("ksyxis");

    @Redirect(
            method = "prepareLevels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    private <T> void servercore$preventAddingRegionTicket(ServerChunkCache chunkCache, TicketType<T> ticketType, ChunkPos chunkPos, int i, T object) {
        // NO-OP
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441))
    private int servercore$preventWaitingForSpawnChunks(int constant) {
        return KSYXIS_LOADED ? constant : 0;
    }
}
