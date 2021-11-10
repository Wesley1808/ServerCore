package org.provim.servercore.interfaces;

public interface ILevel {
    int getRemainingVillagers();

    void setRemainingVillagers(int count);

    int getRemainingAnimals();

    void setRemainingAnimals(int count);

    int getRemainingFlying();

    void setRemainingFlying(int count);

    int getRemainingMonsters();

    void setRemainingMonsters(int count);
}
