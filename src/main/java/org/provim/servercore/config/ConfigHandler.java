package org.provim.servercore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.provim.servercore.ServerCore;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class ConfigHandler {
    private static final File DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "/ServerCore/");
    private static final File CONFIG = new File(DIR, "config.json");
    private static final File MESSAGES = new File(DIR, "messages.json");
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private ConfigHandler() {
    }

    /**
     * Saves the currently loaded config data to file.
     */

    public static void save() {
        if (!DIR.exists()) {
            DIR.mkdirs();
        }
        try (var fw = new FileWriter(CONFIG); var fw2 = new FileWriter(MESSAGES)) {
            fw.write(GSON.toJson(Config.instance()));
            fw2.write(GSON.toJson(MessageConfig.instance()));
        } catch (IOException e) {
            ServerCore.getLogger().error("Failed to save config!", e);
        }
    }

    /**
     * Loads the config data from the file into memory.
     */

    public static void load() {
        if (!CONFIG.exists() || !MESSAGES.exists()) {
            save();
            return;
        }
        try (var fr = new FileReader(CONFIG); FileReader fr2 = new FileReader(MESSAGES)) {
            Config.instance = GSON.fromJson(fr, Config.class);
            MessageConfig.instance = GSON.fromJson(fr2, MessageConfig.class);
        } catch (IOException e) {
            ServerCore.getLogger().error("Failed to load config!", e);
        }
    }
}