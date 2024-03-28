package me.wesley1808.servercore.common.config.legacy;

import java.util.function.Predicate;

public class ConfigEntry<T> {
    private final Predicate<T> constraint;
    private final T defaultValue;
    private final String comment;
    private T value;

    public ConfigEntry(T defaultValue, Predicate<T> constraint, String comment) {
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.comment = comment;
        this.value = defaultValue;
    }

    public ConfigEntry(T defaultValue, Predicate<T> constraint) {
        this(defaultValue, constraint, null);
    }

    public ConfigEntry(T defaultValue, String comment) {
        this(defaultValue, null, comment);
    }

    public ConfigEntry(T defaultValue) {
        this(defaultValue, null, null);
    }

    public T get() {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public T getDefault() {
        return this.defaultValue;
    }

    public Class<?> getType() {
        return this.defaultValue.getClass();
    }

    public boolean set(T value) {
        if (value != null && this.validate(value)) {
            this.value = value;
            return true;
        }

        this.value = this.getFallbackValue();
        return false;
    }

    private boolean validate(T value) {
        final boolean compatible = this.defaultValue.getClass() == value.getClass();
        return this.constraint != null ? compatible && this.constraint.test(value) : compatible;
    }

    private T getFallbackValue() {
        return this.value != null ? this.value : this.defaultValue;
    }
}
