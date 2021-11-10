package org.provim.servercore.mixin.performance.player_login;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
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

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    private static final int PARENT_OF_ITERATOR = 2;
    private static final int ITERATOR = 1;
    private static final int ROOT = 0;

    @Shadow
    @Final
    private Set<Advancement> visible;

    @Shadow
    @Final
    private Set<Advancement> visibilityChanged;

    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancements;

    @Shadow
    @Final
    private Set<Advancement> progressChanged;

    @Shadow
    protected abstract boolean shouldBeVisible(Advancement advancement);

    /**
     * @author Wyatt Childers
     * @reason Optimize the advancement data player iteration to be O(N) rather than O(N^2)
     */

    @Overwrite
    private void ensureVisibility(Advancement advancement) {
        this.ensureVisibility(advancement, ROOT);
    }

    private void ensureVisibility(Advancement advancement, int entryPoint) {
        boolean bl = this.shouldBeVisible(advancement);
        boolean bl2 = this.visible.contains(advancement);
        if (bl && !bl2) {
            this.visible.add(advancement);
            this.visibilityChanged.add(advancement);
            if (this.advancements.containsKey(advancement)) {
                this.progressChanged.add(advancement);
            }
        } else if (!bl && bl2) {
            this.visible.remove(advancement);
            this.visibilityChanged.add(advancement);
        }

        if (bl != bl2 && advancement.getParent() != null) {
            // Paper - If we're not coming from an iterator consider this to be a root entry, otherwise
            // market that we're entering from the parent of an iterator.
            this.ensureVisibility(advancement.getParent(), entryPoint == ITERATOR ? PARENT_OF_ITERATOR : ROOT);
        }

        // If this is true, we've gone through a child iteration, entered the parent, processed the parent
        // and are about to reprocess the children. Stop processing here to prevent O(N^2) processing.
        if (entryPoint == PARENT_OF_ITERATOR) {
            return;
        }

        for (Advancement child : advancement.getChildren()) {
            this.ensureVisibility(child, ITERATOR);
        }
    }
}