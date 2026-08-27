package com.veltavisuals;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeltaVisuals implements ModInitializer {

    public static final String MOD_ID = "veltavisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("VeltaVisuals: common init (version 1.21.11)");
    }
}
