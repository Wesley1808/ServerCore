package org.provim.servercore.config;

import java.util.function.Predicate;

public final class ConfigEntry<T> {
    private final Predicate<T> constraint;
    private final T defaultValue;
    private final String comment;
    private T value;

    public ConfigEntry(T defaultValue, String comment, Predicate<T> constraint) {
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.comment = comment;
    }

    public ConfigEntry(T defaultValue, Predicate<T> constraint) {
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.comment = null;
    }

    public ConfigEntry(T defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.constraint = null;
    }

    public ConfigEntry(T defaultValue) {
        this.defaultValue = defaultValue;
        this.constraint = null;
        this.comment = null;
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

    public void set(T value) {
        this.value = this.validate(value) ? value : this.getFallbackValue();
    }

    private boolean validate(T value) {
        final boolean isValid = this.defaultValue.getClass() == value.getClass();
        return this.constraint != null ? isValid && this.constraint.test(value) : isValid;
    }

    private T getFallbackValue() {
        return this.value != null ? this.value : this.defaultValue;
    }
}
