package me.wesley1808.servercore.common.config.data.impl.activation_range;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.config.data.activation_range.CustomActivationType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.List;

public class CustomActivationTypeImpl extends ActivationTypeImpl implements CustomActivationType {
    private final String name;
    private final List<EntityTypeTest<? super Entity, ?>> matchers;

    public CustomActivationTypeImpl(CustomActivationType source) {
        super(source);
        this.name = source.name();
        this.matchers = new ObjectArrayList<>(source.matchers());
    }

    public CustomActivationTypeImpl(
            String name,
            List<EntityTypeTest<? super Entity, ?>> matchers,
            int activationRange,
            int tickInterval,
            int wakeupInterval,
            boolean extraHeightUp,
            boolean extraHeightDown
    ) {
        super(activationRange, tickInterval, wakeupInterval, extraHeightUp, extraHeightDown);
        this.name = name;
        this.matchers = matchers;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<EntityTypeTest<? super Entity, ?>> matchers() {
        return this.matchers;
    }
}
