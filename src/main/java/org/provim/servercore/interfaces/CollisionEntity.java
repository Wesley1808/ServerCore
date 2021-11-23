package org.provim.servercore.interfaces;

public interface CollisionEntity {

    void setCollisionLoadChunks(boolean enabled);

    boolean shouldCollisionLoadChunks();
}