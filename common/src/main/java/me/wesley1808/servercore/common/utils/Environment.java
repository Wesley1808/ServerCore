package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.services.platform.PlatformHelper;

public class Environment {
    public static final boolean CLIENT = PlatformHelper.isClient();
    public static final boolean MOD_SPARK = PlatformHelper.isModLoaded("spark");
    public static final boolean MOD_VMP = PlatformHelper.isModLoaded("vmp");
    public static final boolean MOD_MOONRISE = PlatformHelper.isModLoaded("moonrise");
}
