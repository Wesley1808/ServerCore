package me.wesley1808.servercore.mixin.optimizations.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.Target;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Mixin(PathFinder.class)
public class PathFinderMixin {
    @Shadow
    @Final
    private NodeEvaluator nodeEvaluator;

    @Redirect(
            method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;stream()Ljava/util/stream/Stream;"
            )
    )
    private Stream<?> servercore$reduceStreams(Set<?> set) {
        return null;
    }

    @Redirect(
            method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Collectors;toMap(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/util/stream/Collector;"
            )
    )
    private Collector<?, ?, ?> servercore$reduceStreams(Function<?, ?> keyMapper, Function<?, ?> valueMapper) {
        return null;
    }

    @Redirect(
            method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"
            )
    )
    private Object servercore$reduceStreams(Stream<?> stream, Collector<?, ?, ?> collector, PathNavigationRegion region, Mob mob, Set<BlockPos> positions) {
        Object2ObjectOpenHashMap<Target, BlockPos> map = new Object2ObjectOpenHashMap<>(positions.size());
        for (BlockPos pos : positions) {
            map.put(this.nodeEvaluator.getTarget(pos.getX(), pos.getY(), pos.getZ()), pos);
        }

        return map;
    }

    @Redirect(
            method = "findPath(Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Sets;newHashSetWithExpectedSize(I)Ljava/util/HashSet;",
                    ordinal = 0,
                    remap = false
            )
    )
    private HashSet<?> servercore$noHashSet(int expectedSize) {
        return null;
    }

    @ModifyVariable(
            method = "findPath(Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            index = 10,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/pathfinder/PathFinder;maxVisitedNodes:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private Set<Target> servercore$replaceSet(Set<Target> nullSet, Node node, Map<Target, BlockPos> positions, float maxRange, int accuracy, float searchDepthMultiplier) {
        return new ObjectArraySet<>();
    }
}
