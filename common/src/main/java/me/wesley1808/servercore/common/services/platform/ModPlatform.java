package me.wesley1808.servercore.common.services.platform;

import java.nio.file.Path;

public interface ModPlatform {
    boolean isModLoaded(String modId);

    Path getConfigDir();

    String getVersion();
}
