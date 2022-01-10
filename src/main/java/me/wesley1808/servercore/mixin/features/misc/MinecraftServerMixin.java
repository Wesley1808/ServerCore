package me.wesley1808.servercore.mixin.features.misc;

import me.wesley1808.servercore.config.tables.FeatureConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MinecraftServer.class, priority = 900)
public abstract class MinecraftServerMixin {

    @Redirect(
            method = "prepareLevels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    private <T> void disableSpawnChunks(ServerChunkCache chunkCache, TicketType<T> ticketType, ChunkPos chunkPos, int i, T object) {
        if (!FeatureConfig.DISABLE_SPAWN_CHUNKS.get()) {
            chunkCache.addRegionTicket(ticketType, chunkPos, i, object);
        }
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441))
    private int disableSpawnChunks(int constant) {
        return FeatureConfig.DISABLE_SPAWN_CHUNKS.get() && !FabricLoader.getInstance().isModLoaded("ksyxis") ? 0 : constant;
    }

    @ModifyConstant(method = "tickServer", constant = @Constant(intValue = 6000), require = 0)
    public int modifyAutoSaveInterval(int constant) {
        return FeatureConfig.AUTO_SAVE_INTERVAL.get() * 1200;
    }
}
