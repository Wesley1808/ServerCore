package me.wesley1808.servercore.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.servercore.ServerCore;
import me.wesley1808.servercore.config.tables.CommandConfig;
import me.wesley1808.servercore.utils.ChunkManager;
import me.wesley1808.servercore.utils.PermissionManager;
import me.wesley1808.servercore.utils.TickManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class StatisticsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var statistics = literal("statistics").requires(src -> PermissionManager.perm(src, PermissionManager.VIEW_STATS, 2));

        statistics.executes(ctx -> displayOverview(ctx.getSource()));

        statistics.then(literal("entities")
                .executes(ctx -> displayEntities(ctx.getSource(), 1))
                .then(argument("page", integer(1))
                        .executes(ctx -> displayEntities(ctx.getSource(), getInteger(ctx, "page")))
                )
        );

        statistics.then(literal("block-entities")
                .executes(ctx -> displayBlockEntities(ctx.getSource(), 1))
                .then(argument("page", integer(1))
                        .executes(ctx -> displayBlockEntities(ctx.getSource(), getInteger(ctx, "page")))
                )
        );


        dispatcher.register(statistics);
    }

    private static int displayOverview(CommandSourceStack source) {
        final double mspt = TickManager.getAverageTickTime();
        final double tps = mspt != 0 ? Math.min((1000 / mspt), 20) : 20;

        TextComponent component = new TextComponent(CommandConfig.STATS_OVERVIEW_CONTENT.get()
                .replace("%TPS%", String.format("%.2f", tps))
                .replace("%MSPT%", String.format("%.2f", mspt))
                .replace("%CHUNK_COUNT%", String.valueOf(getLoadedChunkCount()))
                .replace("%ENTITY_COUNT%", String.valueOf(getAllEntities().size()))
                .replace("%BLOCK_ENTITY_COUNT%", String.valueOf(getAllBlockEntities().size()))
        );

        source.sendSuccess(component, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int displayEntities(CommandSourceStack source, int page) {
        TextComponent component = new TextComponent(CommandConfig.STATS_PAGE_TITLE.get()
                .replace("%TITLE%", "Entity Count")
                .replace("%PAGE%", String.valueOf(page))
        );

        var entitiesByCount = sortByValue(getEntitiesByCount(getAllEntities()));
        boolean success = displayPage(entitiesByCount, page, (entry, index) -> appendEntry(component, EntityType.getKey(entry.getKey()), index, entry.getValue()));

        if (success) {
            source.sendSuccess(component, false);
        } else if (page == 1) {
            source.sendFailure(new TextComponent("No entities were found!"));
        } else {
            source.sendFailure(new TextComponent("Page doesn't exist!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int displayBlockEntities(CommandSourceStack source, int page) {
        TextComponent component = new TextComponent(CommandConfig.STATS_PAGE_TITLE.get()
                .replace("%TITLE%", "Block Entity Count")
                .replace("%PAGE%", String.valueOf(page))
        );

        var blockEntitiesByCount = sortByValue(getBlockEntitiesByCount(getAllBlockEntities()));
        boolean success = displayPage(blockEntitiesByCount, page, (entry, index) -> appendEntry(component, BlockEntityType.getKey(entry.getKey()), index, entry.getValue()));

        if (success) {
            source.sendSuccess(component, false);
        } else if (page == 1) {
            source.sendFailure(new TextComponent("No block entities were found!"));
        } else {
            source.sendFailure(new TextComponent("Page doesn't exist!"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void appendEntry(TextComponent component, ResourceLocation location, int index, int count) {
        if (location != null) {
            component.append(new TextComponent("\n" + CommandConfig.STATS_PAGE_CONTENT.get()
                    .replace("%NAME%", location.toString())
                    .replace("%INDEX%", String.valueOf(index))
                    .replace("%COUNT%", String.valueOf(count)))
            );
        }
    }

    private static <T> boolean displayPage(List<T> list, int page, BiConsumer<T, Integer> consumer) {
        int index = 9 * (page - 1);
        int toIndex = Math.min(index + 9, list.size());

        if (toIndex <= index) {
            return false;
        }

        for (T value : list.subList(index, toIndex)) {
            consumer.accept(value, ++index);
        }

        return true;
    }

    private static ImmutableList<Entity> getAllEntities() {
        ImmutableList.Builder<Entity> builder = ImmutableList.builder();
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                builder.add(entity);
            }
        }

        return builder.build();
    }

    private static ImmutableList<BlockEntity> getAllBlockEntities() {
        ImmutableList.Builder<BlockEntity> builder = ImmutableList.builder();
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            for (ChunkHolder holder : level.getChunkSource().chunkMap.getChunks()) {
                LevelChunk chunk = ChunkManager.getChunkIfLoaded(holder);
                if (chunk != null) {
                    builder.addAll(chunk.getBlockEntities().values());
                }
            }
        }

        return builder.build();
    }

    private static int getLoadedChunkCount() {
        int count = 0;
        for (ServerLevel level : ServerCore.getServer().getAllLevels()) {
            for (ChunkHolder holder : level.getChunkSource().chunkMap.getChunks()) {
                if (ChunkManager.isChunkLoaded(holder)) {
                    count++;
                }
            }
        }

        return count;
    }

    private static Map<EntityType<?>, Integer> getEntitiesByCount(Iterable<Entity> entities) {
        Map<EntityType<?>, Integer> map = new HashMap<>();
        for (Entity entity : entities) {
            EntityType<?> type = entity.getType();
            map.put(type, map.getOrDefault(type, 0) + 1);
        }

        return map;
    }

    private static Map<BlockEntityType<?>, Integer> getBlockEntitiesByCount(Iterable<BlockEntity> blockEntities) {
        Map<BlockEntityType<?>, Integer> map = new HashMap<>();
        for (BlockEntity blockEntity : blockEntities) {
            BlockEntityType<?> type = blockEntity.getType();
            map.put(type, map.getOrDefault(type, 0) + 1);
        }

        return map;
    }

    private static <K, V extends Comparable<V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        entries.sort((c1, c2) -> c2.getValue().compareTo(c1.getValue()));
        return entries;
    }
}
