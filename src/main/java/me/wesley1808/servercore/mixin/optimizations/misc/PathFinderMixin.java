package me.wesley1808.servercore.mixin.optimizations.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.*;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Mixin(PathFinder.class)
public abstract class PathFinderMixin {
    @Unique
    private static final Comparator<Path> DEFAULT_COMPARATOR = Comparator.comparingInt(Path::getNodeCount);

    @Unique
    private static final Comparator<Path> REACHED_COMPARATOR = Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount);

    @Shadow
    @Final
    private NodeEvaluator nodeEvaluator;

    @Shadow
    protected abstract Path reconstructPath(Node point, BlockPos targetPos, boolean reachesTarget);

    /**
     * Removes streams from {@link PathFinder#findPath(PathNavigationRegion, Mob, Set, float, int, float)}
     * And replaces the map with fastutil.
     */
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
    private Object servercore$reduceStreams(Stream<?> stream, Collector<?, ?, ?> collector) {
        return null;
    }

    @ModifyVariable(
            method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            index = 8,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/PathNavigationRegion;getProfiler()Lnet/minecraft/util/profiling/ProfilerFiller;",
                    shift = At.Shift.BEFORE
            )
    )
    private Map<Target, BlockPos> servercore$replaceMap(Map<Target, BlockPos> nullMap, PathNavigationRegion region, Mob mob, Set<BlockPos> positions, float maxRange, int accuracy, float searchDepthMultiplier) {
        Object2ObjectOpenHashMap<Target, BlockPos> map = new Object2ObjectOpenHashMap<>(positions.size());
        for (BlockPos pos : positions) {
            map.put(this.nodeEvaluator.getGoal(pos.getX(), pos.getY(), pos.getZ()), pos);
        }

        return map;
    }

    /**
     * Removes streams from {@link PathFinder#findPath(ProfilerFiller, Node, Map, float, int, float)}
     * And replaces the hashset with fastutil.
     */
    @Redirect(
            method = "findPath(Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Sets;newHashSetWithExpectedSize(I)Ljava/util/HashSet;",
                    ordinal = 0
            )
    )
    private HashSet<?> servercore$noHashSet(int expectedSize) {
        return null;
    }

    @ModifyVariable(
            method = "findPath(Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            index = 10,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/pathfinder/PathFinder;maxVisitedNodes:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private Set<Target> servercore$replaceSet(Set<Target> nullSet, ProfilerFiller profiler, Node node, Map<Target, BlockPos> positions, float maxRange, int accuracy, float searchDepthMultiplier) {
        return new ObjectArraySet<>();
    }

    @Inject(
            method = "findPath(Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;isEmpty()Z",
                    shift = At.Shift.BEFORE,
                    ordinal = 1
            )
    )
    private void servercore$reduceStreams(ProfilerFiller profiler, Node node, Map<Target, BlockPos> positions, float maxRange, int accuracy, float searchDepthMultiplier, CallbackInfoReturnable<@Nullable Path> cir, Set<Target> allTargets, Set<Target> reachedTargets) {
        boolean reachedEmpty = reachedTargets.isEmpty();
        Comparator<Path> comparator = reachedEmpty ? DEFAULT_COMPARATOR : REACHED_COMPARATOR;

        Path best = null;
        for (Target target : reachedEmpty ? allTargets : reachedTargets) {
            Path path = this.reconstructPath(target.getBestNode(), positions.get(target), reachedEmpty);
            if (best == null || comparator.compare(path, best) < 0) {
                best = path;
            }
        }

        profiler.pop();
        cir.setReturnValue(best);
    }
}
