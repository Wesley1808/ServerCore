package me.wesley1808.servercore.common.interfaces.activation_range;

public interface IGoalSelector {
    boolean inactiveTick(int tickRate, boolean inactive);

    boolean hasTasks();
}