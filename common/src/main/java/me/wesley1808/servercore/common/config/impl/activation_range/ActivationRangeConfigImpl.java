package me.wesley1808.servercore.common.config.impl.activation_range;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.wesley1808.servercore.common.config.data.activation_range.ActivationRangeConfig;
import me.wesley1808.servercore.common.config.data.activation_range.ActivationType;
import me.wesley1808.servercore.common.config.data.activation_range.CustomActivationType;
import me.wesley1808.servercore.common.utils.Util;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Set;

public class ActivationRangeConfigImpl implements ActivationRangeConfig {
    private final boolean enabled;
    private final boolean tickNewEntities;
    private final boolean useVerticalRange;
    private final boolean skipNonImmune;
    private final boolean villagerTickPanic;
    private final int villagerWorkImmunityAfter;
    private final int villagerWorkImmunityFor;
    private final Set<EntityType<?>> excludedEntityTypes;
    private final ActivationType defaultActivationType;
    private final List<CustomActivationType> activationTypes;

    public ActivationRangeConfigImpl(ActivationRangeConfig source) {
        this.enabled = source.enabled();
        this.tickNewEntities = source.tickNewEntities();
        this.useVerticalRange = source.useVerticalRange();
        this.skipNonImmune = source.skipNonImmune();
        this.villagerTickPanic = source.villagerTickPanic();
        this.villagerWorkImmunityAfter = source.villagerWorkImmunityAfter();
        this.villagerWorkImmunityFor = source.villagerWorkImmunityFor();
        this.excludedEntityTypes = new ReferenceOpenHashSet<>(source.excludedEntityTypes());
        this.defaultActivationType = new ActivationTypeImpl(source.defaultActivationType());
        this.activationTypes = Util.map(source.activationTypes(), CustomActivationTypeImpl::new);
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public boolean tickNewEntities() {
        return this.tickNewEntities;
    }

    @Override
    public boolean useVerticalRange() {
        return this.useVerticalRange;
    }

    @Override
    public boolean skipNonImmune() {
        return this.skipNonImmune;
    }

    @Override
    public boolean villagerTickPanic() {
        return this.villagerTickPanic;
    }

    @Override
    public int villagerWorkImmunityAfter() {
        return this.villagerWorkImmunityAfter;
    }

    @Override
    public int villagerWorkImmunityFor() {
        return this.villagerWorkImmunityFor;
    }

    @Override
    public Set<EntityType<?>> excludedEntityTypes() {
        return this.excludedEntityTypes;
    }

    @Override
    public ActivationType defaultActivationType() {
        return this.defaultActivationType;
    }

    @Override
    public List<CustomActivationType> activationTypes() {
        return this.activationTypes;
    }
}
