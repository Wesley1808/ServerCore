package me.wesley1808.servercore.common.interfaces.activation_range;

import me.wesley1808.servercore.common.config.data.activation_range.ActivationType;

public interface ActivationEntity {
    ActivationType servercore$getActivationType();

    boolean servercore$isExcluded();

    int servercore$getActivatedTick();

    void servercore$setActivatedTick(int activatedTick);

    int servercore$getActivatedImmunityTick();

    void servercore$setActivatedImmunityTick(int activatedImmunityTick);

    boolean servercore$isInactive();

    void servercore$setInactive(boolean active);

    void servercore$incFullTickCount();

    default int servercore$getFullTickCount() {
        return 0;
    }
}
