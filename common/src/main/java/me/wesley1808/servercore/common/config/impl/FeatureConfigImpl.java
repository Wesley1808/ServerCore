package me.wesley1808.servercore.common.config.impl;

import me.wesley1808.servercore.common.config.data.FeatureConfig;

public class FeatureConfigImpl implements FeatureConfig {
    private final boolean preventEnderpearlChunkLoading;
    private final boolean chunkTickDistanceAffectsRandomTicks;
    private final boolean preventMovingIntoUnloadedChunks;
    private final int autosaveIntervalSeconds;
    private final int xpMergeChance;
    private final double xpMergeRadius;
    private final double itemMergeRadius;
    private final boolean lobotomizeVillagers;
    private final int lobotomizedTickInterval;

    public FeatureConfigImpl(FeatureConfig source) {
        this.preventEnderpearlChunkLoading = source.preventEnderpearlChunkLoading();
        this.chunkTickDistanceAffectsRandomTicks = source.chunkTickDistanceAffectsRandomTicks();
        this.preventMovingIntoUnloadedChunks = source.preventMovingIntoUnloadedChunks();
        this.autosaveIntervalSeconds = source.autosaveIntervalSeconds();
        this.xpMergeChance = source.xpMergeFraction();
        this.xpMergeRadius = source.xpMergeRadius();
        this.itemMergeRadius = source.itemMergeRadius();
        this.lobotomizeVillagers = source.lobotomizeVillagers();
        this.lobotomizedTickInterval = source.lobotomizedTickInterval();
    }

    @Override
    public boolean preventEnderpearlChunkLoading() {
        return this.preventEnderpearlChunkLoading;
    }

    @Override
    public boolean chunkTickDistanceAffectsRandomTicks() {
        return this.chunkTickDistanceAffectsRandomTicks;
    }

    @Override
    public boolean preventMovingIntoUnloadedChunks() {
        return this.preventMovingIntoUnloadedChunks;
    }

    @Override
    public int autosaveIntervalSeconds() {
        return this.autosaveIntervalSeconds;
    }

    @Override
    public int xpMergeFraction() {
        return this.xpMergeChance;
    }

    @Override
    public double xpMergeRadius() {
        return this.xpMergeRadius;
    }

    @Override
    public double itemMergeRadius() {
        return this.itemMergeRadius;
    }

    @Override
    public boolean lobotomizeVillagers() {
        return this.lobotomizeVillagers;
    }

    @Override
    public int lobotomizedTickInterval() {
        return this.lobotomizedTickInterval;
    }
}
