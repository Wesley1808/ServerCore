package me.wesley1808.servercore.common.interfaces.activation_range;

import me.wesley1808.servercore.common.activation_range.ActivationType;

public interface ActivationEntity {
    ActivationType getActivationType();

    boolean isExcluded();

    int getActivatedTick();

    void setActivatedTick(int activatedTick);

    int getActivatedImmunityTick();

    void setActivatedImmunityTick(int activatedImmunityTick);

    void setInactive(boolean active);

    void incFullTickCount();

    default int getFullTickCount() {
        return 0;
    }
}
