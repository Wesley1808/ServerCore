package org.provim.servercore.interfaces;

public interface IWorld {
    int getRemainingVillagers();

    void setRemainingVillagers(int count);

    int getRemainingAnimals();

    void setRemainingAnimals(int count);

    int getRemainingFlying();

    void setRemainingFlying(int count);

    int getRemainingMonsters();

    void setRemainingMonsters(int count);
}
