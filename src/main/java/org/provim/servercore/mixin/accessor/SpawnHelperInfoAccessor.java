package org.provim.servercore.mixin.accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnHelper.Info.class)
public interface SpawnHelperInfoAccessor {

    @Accessor("spawningChunkCount")
    int getSpawnChunkCount();

    @Invoker("test")
    boolean check(EntityType<?> type, BlockPos pos, Chunk chunk);

    @Invoker("run")
    void runMob(MobEntity mobEntity, Chunk chunk);
}