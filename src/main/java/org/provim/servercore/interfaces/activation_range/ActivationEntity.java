package org.provim.servercore.interfaces.activation_range;

import org.provim.servercore.utils.ActivationRange;

public interface ActivationEntity {

    ActivationRange.ActivationType getActivationType();

    boolean isExcluded();

    int getActivatedTick();

    void setActivatedTick(int activatedTick);

    int getActivatedImmunityTick();

    void setActivatedImmunityTick(int activatedImmunityTick);

    void setInactive(boolean active);

    int getFullTickCount();

    void incFullTickCount();
}
