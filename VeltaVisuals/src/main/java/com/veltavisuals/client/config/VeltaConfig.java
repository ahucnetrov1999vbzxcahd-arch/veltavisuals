package com.veltavisuals.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Простой JSON-конфиг мода. Хранит только визуальные/эстетические
 * настройки — никаких игровых "advantage" фич.
 */
public class VeltaConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("veltavisuals.json");

    private static VeltaConfig INSTANCE;

    // --- HUD ---
    public boolean hudEnabled = true;
    public boolean showCoordinates = true;
    public boolean showFps = true;
    public boolean showClock = true;
    public boolean showCompass = true;

    // --- Эстетика мира ---
    public boolean ambientParticlesEnabled = true;
    public boolean customWeatherShaderEnabled = true;
    public boolean dynamicSkyTintEnabled = true;

    // --- Меню ---
    public String menuTheme = "aurora"; // пресеты темы красивого меню
    public float menuAnimationSpeed = 1.0f;

    public static VeltaConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                INSTANCE = GSON.fromJson(reader, VeltaConfig.class);
            } catch (IOException e) {
                INSTANCE = new VeltaConfig();
            }
        } else {
            INSTANCE = new VeltaConfig();
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("VeltaVisuals: не удалось сохранить конфиг", e);
        }
    }
}
