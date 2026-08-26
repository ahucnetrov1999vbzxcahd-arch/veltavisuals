package com.veltavisuals.client.hud;

import com.veltavisuals.client.config.VeltaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Отрисовка эстетичного HUD поверх игры.
 * Только информационные элементы — без ESP/трекинга других игроков.
 */
public class HudRenderer {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void render(DrawContext ctx, MinecraftClient client) {
        VeltaConfig cfg = VeltaConfig.get();
        int x = 6;
        int y = 6;
        int lineHeight = 10;
        int color = 0xFFE0E0FF; // мягкий сиреневый оттенок под тему "aurora"

        if (cfg.showCoordinates && client.player != null) {
            BlockPos pos = client.player.getBlockPos();
            String text = String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ());
            ctx.drawTextWithShadow(client.textRenderer, text, x, y, color);
            y += lineHeight;
        }

        if (cfg.showFps) {
            String text = "FPS: " + client.getCurrentFps();
            ctx.drawTextWithShadow(client.textRenderer, text, x, y, color);
            y += lineHeight;
        }

        if (cfg.showClock) {
            String text = "Time: " + LocalTime.now().format(TIME_FMT);
            ctx.drawTextWithShadow(client.textRenderer, text, x, y, color);
            y += lineHeight;
        }

        if (cfg.showCompass && client.player != null) {
            float yaw = client.player.getYaw() % 360;
            if (yaw < 0) yaw += 360;
            String direction = compassDirection(yaw);
            ctx.drawTextWithShadow(client.textRenderer, "Facing: " + direction, x, y, color);
        }
    }

    private static String compassDirection(float yaw) {
        String[] dirs = {"S", "SW", "W", "NW", "N", "NE", "E", "SE", "S"};
        int index = Math.round(yaw / 45f);
        return dirs[index];
    }
}
