package me.wesley1808.servercore.common.utils.statistics.entry;

import org.jetbrains.annotations.NotNull;

public class StatisticEntry<T> implements Comparable<StatisticEntry<T>> {
    protected int count;

    public void increment(T value) {
        this.count++;
    }

    public String formatValue() {
        return String.format("<c:#secondary>%d</c>", this.count);
    }

    @Override
    public int compareTo(@NotNull StatisticEntry<T> other) {
        return Integer.compare(this.count, other.count);
    }
}
