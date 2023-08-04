package me.wesley1808.servercore.common.interfaces.activation_range;

import me.wesley1808.servercore.common.activation_range.ActivationType;

public interface LevelInfo {
    int servercore$getRemaining(ActivationType.Wakeup key);

    void servercore$setRemaining(ActivationType.Wakeup key, int value);
}
