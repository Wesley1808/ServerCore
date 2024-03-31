package me.wesley1808.servercore.common.utils.statistics.entry;

import me.wesley1808.servercore.common.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class EntityStatisticEntry extends StatisticEntry<Entity> {
    private int activeCount;

    @Override
    public void increment(Entity entity) {
        super.increment(entity);

        if (entity.level() instanceof ServerLevel level && this.isActive(entity, level)) {
            this.activeCount++;
        }
    }

    @Override
    public String formatValue() {
        if (Config.get().activationRange().enabled()) {
            int percentage = Math.round(this.activeCount / (float) this.count * 100);
            return String.format("<c:#secondary>%d</c> <dark_gray>|</dark_gray> <gray>Active: %d%%</gray>", this.count, percentage);
        } else {
            return super.formatValue();
        }
    }

    private boolean isActive(Entity entity, ServerLevel level) {
        return !entity.servercore$isInactive() && level.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().toLong());
    }
}
