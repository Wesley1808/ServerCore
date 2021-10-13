package org.provim.servercore.config;

public final class ConfigEntry<T> {
    private final T defaultValue;
    private final String comment;
    private T value;

    public ConfigEntry(T defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    public ConfigEntry(T defaultValue) {
        this.defaultValue = defaultValue;
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
        return this.defaultValue.getClass() == value.getClass();
    }

    private T getFallbackValue() {
        return this.value != null ? this.value : this.defaultValue;
    }
}
