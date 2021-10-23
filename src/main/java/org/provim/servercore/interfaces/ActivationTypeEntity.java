package org.provim.servercore.interfaces;

import org.provim.servercore.utils.ActivationType;

public interface ActivationTypeEntity {

    ActivationType getActivationType();

    boolean isExcluded();

    int getActivatedTick();

    void setActivatedTick(int activatedTick);
}
