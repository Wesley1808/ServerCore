package org.provim.servercore.interfaces;

import org.provim.servercore.utils.ActivationRange;

public interface ActivationEntity {

    ActivationRange.ActivationType getActivationType();

    boolean isExcluded();

    int getActivatedTick();

    void setActivatedTick(int activatedTick);

    int getActivatedImmunityTick();

    void setActivatedImmunityTick(int activatedImmunityTick);

    void setInactive(boolean active);

    void setTemporarilyActive(boolean active);

    int getFullTickCount();

    void incFullTickCount();
}
