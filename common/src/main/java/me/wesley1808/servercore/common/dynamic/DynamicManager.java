package me.wesley1808.servercore.common.dynamic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.servercore.common.config.Config;
import me.wesley1808.servercore.common.config.data.dynamic.DynamicConfig;
import me.wesley1808.servercore.common.config.data.dynamic.Setting;
import me.wesley1808.servercore.common.interfaces.IMinecraftServer;
import me.wesley1808.servercore.common.interfaces.IMobCategory;
import me.wesley1808.servercore.common.utils.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public class DynamicManager {
    private static final List<LinkedSetting> SETTINGS = new ObjectArrayList<>();
    private final MinecraftServer server;
    private double averageTickTime;
    private int count;

    public DynamicManager(MinecraftServer server) {
        this.server = server;
        DynamicSetting.initDefaultValues();
    }

    public static DynamicManager getInstance(MinecraftServer server) {
        return ((IMinecraftServer) server).servercore$getDynamicManager();
    }

    public static void reload() {
        SETTINGS.clear();

        List<Setting> settings = Config.get().dynamic().settings();
        DynamicSetting.recalculateValues(settings);

        for (Setting setting : settings) {
            SETTINGS.add(new LinkedSetting(setting));
        }

        for (int i = 0; i < SETTINGS.size(); i++) {
            LinkedSetting linked = SETTINGS.get(i);
            linked.initialize(
                    i == 0 ? null : SETTINGS.get(i - 1), // prev
                    i == SETTINGS.size() - 1 ? null : SETTINGS.get(i + 1) // next
            );
        }
    }

    public static void update(MinecraftServer server) {
        if (server.getTickCount() % 20 == 0) {
            DynamicManager manager = getInstance(server);
            manager.updateValues();

            DynamicConfig config = Config.get().dynamic();
            if (config.enabled()) {
                manager.runPerformanceChecks(config);
            }
        }
    }

    private void updateValues() {
        this.averageTickTime = this.calculateAverageTickTime();
        this.count++;
    }

    protected double calculateAverageTickTime() {
        return this.server.getCurrentSmoothedTickTime();
    }

    private void runPerformanceChecks(DynamicConfig config) {
        final double targetMspt = config.targetMspt();
        final boolean decrease = this.averageTickTime > targetMspt + 5;
        final boolean increase = this.averageTickTime < Math.max(targetMspt - 5, 2);

        if (decrease || increase) {
            Iterable<LinkedSetting> ordered = increase ? SETTINGS.reversed() : SETTINGS;
            for (LinkedSetting setting : ordered) {
                if (setting.shouldRun(this.count) && setting.modify(increase, this)) {
                    break;
                }
            }
        }
    }

    public void modifyViewDistance(int distance) {
        this.server.getPlayerList().setViewDistance(distance);
        if (Environment.CLIENT) {
            Minecraft.getInstance().options.renderDistance().set(distance);
        }
    }

    public void modifySimulationDistance(int distance) {
        this.server.getPlayerList().setSimulationDistance(distance);
        if (Environment.CLIENT) {
            Minecraft.getInstance().options.simulationDistance().set(distance);
        }
    }

    public static void modifyMobcaps(int percentage) {
        final double modifier = percentage / 100F;
        for (MobCategory category : MobCategory.values()) {
            IMobCategory.modifyCapacity(category, modifier);
        }
    }

    public double getAverageTickTime() {
        return this.averageTickTime;
    }
}