package org.provim.servercore.config;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ConfigEntry<T> {
    private final Predicate<T> constraint;
    private final Consumer<T> onChanged;
    private final T defaultValue;
    private final String comment;
    private T value;

    public ConfigEntry(T defaultValue, Predicate<T> constraint, Consumer<T> onChanged, String comment) {
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.onChanged = onChanged;
        this.comment = comment;
    }

    public ConfigEntry(T defaultValue, Predicate<T> constraint, String comment) {
        this(defaultValue, constraint, null, comment);
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
        final T oldValue = this.value;
        final boolean modified = this.modify(value);

        if (this.onChanged != null && oldValue != this.value) {
            this.onChanged.accept(this.value);
        }

        return modified;
    }

    private boolean modify(T value) {
        if (this.validate(value)) {
            this.value = value;
            return true;
        }

        this.value = this.getFallbackValue();
        return false;
    }

    private boolean validate(T value) {
        final boolean isValid = this.defaultValue.getClass() == value.getClass();
        return this.constraint != null ? isValid && this.constraint.test(value) : isValid;
    }

    private T getFallbackValue() {
        return this.value != null ? this.value : this.defaultValue;
    }
}
