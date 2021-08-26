package org.provim.servercore.mixin.accessor;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GravityField;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnHelper.Info.class)
public interface SpawnHelperInfoAccessor {

    @Invoker("<init>")
    static SpawnHelper.Info init(int spawningChunkCount, Object2IntOpenHashMap<SpawnGroup> groupToCount, GravityField gravityField) {
        throw new AssertionError();
    }

    @Accessor("spawningChunkCount")
    int getSpawnChunkCount();

    @Invoker("test")
    boolean check(EntityType<?> type, BlockPos pos, Chunk chunk);

    @Invoker("run")
    void runMob(MobEntity mobEntity, Chunk chunk);
}