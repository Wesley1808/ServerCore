package me.wesley1808.servercore.common.utils.statistics.entry;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.MainConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class EntityStatisticEntry extends StatisticEntry<Entity> {
    private int activeCount;

    @Override
    public void increment(Entity entity) {
        super.increment(entity);

        if (entity.level() instanceof ServerLevel level && this.isActive(entity, level)) {
            this.activeCount++;
            System.out.println(activeCount);
        }
    }

    @Override
    public String formatValue() {
        MainConfig config = Config.get();
        if (config.activationRange().enabled()) {
            int percentage = Math.round(this.activeCount / (float) this.count * 100);
            return String.format("<c:%s>%d</c> <dark_gray>|</dark_gray> <gray>Active: %d%%</gray>", config.commands().secondaryHex(), this.count, percentage);
        } else {
            return super.formatValue();
        }
    }

    private boolean isActive(Entity entity, ServerLevel level) {
        return !entity.servercore$isInactive() && level.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().toLong());
    }
}
