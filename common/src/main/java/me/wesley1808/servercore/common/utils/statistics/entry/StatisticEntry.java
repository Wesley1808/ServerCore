package me.wesley1808.servercore.common.utils.statistics.entry;

import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.CommandConfig;
import org.jetbrains.annotations.NotNull;

public class StatisticEntry<T> implements Comparable<StatisticEntry<T>> {
    protected int count;

    public void increment(T value) {
        this.count++;
    }

    public String formatValue() {
        CommandConfig config = Config.get().commands();
        return String.format("<c:%s>%d</c>", config.secondaryHex(), this.count);
    }

    @Override
    public int compareTo(@NotNull StatisticEntry<T> other) {
        return Integer.compare(this.count, other.count);
    }
}
