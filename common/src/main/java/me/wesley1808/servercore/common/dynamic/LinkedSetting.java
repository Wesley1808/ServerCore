package me.wesley1808.servercore.common.dynamic;

import me.wesley1808.servercore.common.config.data.dynamic.Setting;

public class LinkedSetting {
    private final DynamicSetting dynamicSetting;
    private final int max;
    private final int min;
    private final int increment;
    private final int interval;
    private LinkedSetting prev;
    private LinkedSetting next;

    public LinkedSetting(Setting config) {
        this.dynamicSetting = config.dynamicSetting();
        this.max = config.max();
        this.min = config.min();
        this.increment = config.increment();
        this.interval = config.interval();
    }

    public void initialize(LinkedSetting prev, LinkedSetting next) {
        this.prev = prev;
        this.next = next;
    }

    public boolean shouldRun(int count) {
        return this.interval > 0 && count % this.interval == 0;
    }

    public boolean modify(boolean increase, DynamicManager manager) {
        int value = this.newValue(increase);
        if (this.shouldModify(value)) {
            this.dynamicSetting.set(value, manager);
            return true;
        }
        return false;
    }

    private boolean shouldModify(int value) {
        int compared = Integer.compare(value, this.dynamicSetting.get());
        return compared != 0 && (this.next != null || this.prev != null)
               && (compared < 0 || (!this.isMaximum() && (this.next == null || this.next.isMaximum())))
               && (compared > 0 || (!this.isMinimum() && (this.prev == null || this.prev.isMinimum())));
    }

    private int newValue(boolean increase) {
        return increase
                ? Math.min(this.dynamicSetting.get() + this.increment, this.max)
                : Math.max(this.dynamicSetting.get() - this.increment, this.min);
    }

    private boolean isMinimum() {
        return this.dynamicSetting.get() <= this.min;
    }

    private boolean isMaximum() {
        return this.dynamicSetting.get() >= this.max;
    }
}
