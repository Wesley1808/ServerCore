package me.wesley1808.servercore.common.utils;

import me.wesley1808.servercore.common.services.platform.PlatformHelper;

public class ModCompat {
    public static final boolean SPARK = PlatformHelper.isModLoaded("spark");
    public static final boolean VMP = PlatformHelper.isModLoaded("vmp");
}
