package org.provim.servercore.mixin.performance.player_login;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

/**
 * From: PaperMC (Optimize-the-advancement-data-player-iteration-to-be.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    private static final int PARENT_OF_ITERATOR = 2;
    private static final int ITERATOR = 1;
    private static final int ROOT = 0;

    @Shadow
    @Final
    private Set<Advancement> progressUpdates;

    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancementToProgress;

    @Shadow
    @Final
    private Set<Advancement> visibilityUpdates;

    @Shadow
    @Final
    private Set<Advancement> visibleAdvancements;

    @Shadow
    protected abstract boolean canSee(Advancement advancement);

    /**
     * @author Wyatt Childers
     * @reason Optimize the advancement data player iteration to be O(N) rather than O(N^2)
     */

    @Overwrite
    private void updateDisplay(Advancement advancement) {
        this.updateDisplay(advancement, ROOT);
    }

    private void updateDisplay(Advancement advancement, int entryPoint) {
        boolean bl = this.canSee(advancement);
        boolean bl2 = this.visibleAdvancements.contains(advancement);
        if (bl && !bl2) {
            this.visibleAdvancements.add(advancement);
            this.visibilityUpdates.add(advancement);
            if (this.advancementToProgress.containsKey(advancement)) {
                this.progressUpdates.add(advancement);
            }
        } else if (!bl && bl2) {
            this.visibleAdvancements.remove(advancement);
            this.visibilityUpdates.add(advancement);
        }

        if (bl != bl2 && advancement.getParent() != null) {
            // Paper - If we're not coming from an iterator consider this to be a root entry, otherwise
            // market that we're entering from the parent of an iterator.
            this.updateDisplay(advancement.getParent(), entryPoint == ITERATOR ? PARENT_OF_ITERATOR : ROOT);
        }

        // If this is true, we've gone through a child iteration, entered the parent, processed the parent
        // and are about to reprocess the children. Stop processing here to prevent O(N^2) processing.
        if (entryPoint == PARENT_OF_ITERATOR) {
            return;
        }

        for (Advancement child : advancement.getChildren()) {
            this.updateDisplay(child, ITERATOR);
        }
    }
}