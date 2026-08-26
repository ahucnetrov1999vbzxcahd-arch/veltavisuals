package com.veltavisuals;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VeltaVisuals — эстетический клиентский визуальный мод.
 * Версия под Minecraft 1.21.11 (Fabric).
 *
 * Общая (common) точка входа. Вся клиентская логика (меню, HUD, keybind)
 * находится в пакете client, инициализируется через VeltaVisualsClient
 * (ClientModInitializer), т.к. это чисто визуальный клиентский мод.
 */
public class VeltaVisuals implements ModInitializer {

    public static final String MOD_ID = "veltavisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("VeltaVisuals: common init (version 1.21.11)");
        // Здесь регистрируются только общие вещи (если появятся:
        // например, сетевые пакеты синхронизации конфига на сервере).
        // Никакой игровой логики/читов — мод чисто визуальный.
    }
}
