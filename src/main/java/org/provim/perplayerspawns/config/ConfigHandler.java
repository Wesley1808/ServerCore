package org.provim.perplayerspawns.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class ConfigHandler {
    private static final File DIR = FabricLoader.getInstance().getConfigDir().toFile();
    private static final File CONFIG = new File(DIR, "perplayerspawns.json");
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger("Per-Player-Spawns");

    private ConfigHandler() {
    }

    /**
     * Saves the currently loaded config data to file.
     */

    public static void save() {
        if (!DIR.exists()) {
            DIR.mkdirs();
        }
        try (var fw = new FileWriter(CONFIG)) {
            fw.write(GSON.toJson(Config.instance()));
        } catch (IOException e) {
            LOGGER.error("Failed to save config!", e);
        }
    }

    /**
     * Loads the config data from the file into memory.
     */

    public static void load() {
        if (!CONFIG.exists()) {
            save();
            return;
        }
        try (var fr = new FileReader(CONFIG)) {
            Config.instance = GSON.fromJson(fr, Config.class);
        } catch (IOException e) {
            LOGGER.error("Failed to load config!", e);
        }
    }
}
