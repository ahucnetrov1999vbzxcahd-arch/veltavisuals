package com.veltavisuals.client;

import com.veltavisuals.VeltaVisuals;
import com.veltavisuals.client.config.VeltaConfig;
import com.veltavisuals.client.gui.VeltaMenuScreen;
import com.veltavisuals.client.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Клиентская инициализация: keybind на правый Shift открывает
 * VeltaMenuScreen, плюс подписка на отрисовку HUD.
 */
public class VeltaVisualsClient implements ClientModInitializer {

    // Категория для экрана "Controls" в настройках Minecraft
    private static final String CATEGORY = "key.categories.veltavisuals";

    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        VeltaConfig.load();

        // Регистрация клавиши: правый Shift, переназначаемая игроком в настройках
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.veltavisuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));

        // Проверка нажатия каждый клиентский тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new VeltaMenuScreen());
                }
            }
        });

        // Регистрация HUD-рендера (координаты/FPS/часы/компас и т.д.)
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (VeltaConfig.get().hudEnabled && client.player != null) {
                HudRenderer.render(drawContext, client);
            }
        });

        VeltaVisuals.LOGGER.info("VeltaVisuals: client init, keybind = Right Shift");
    }
}
