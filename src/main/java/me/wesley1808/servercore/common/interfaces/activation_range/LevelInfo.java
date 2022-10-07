package me.wesley1808.servercore.common.interfaces.activation_range;

public interface LevelInfo {
    int getRemainingVillagers();

    void setRemainingVillagers(int count);

    int getRemainingAnimals();

    void setRemainingAnimals(int count);

    int getRemainingFlying();

    void setRemainingFlying(int count);

    int getRemainingMonsters();

    void setRemainingMonsters(int count);
}
