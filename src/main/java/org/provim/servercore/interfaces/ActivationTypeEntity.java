package org.provim.servercore.interfaces;

import org.provim.servercore.utils.activation_range.ActivationType;

public interface ActivationTypeEntity {

    ActivationType getActivationType();

    boolean isExcluded();

    int getActivatedTick();

    void setActivatedTick(int activatedTick);
}
