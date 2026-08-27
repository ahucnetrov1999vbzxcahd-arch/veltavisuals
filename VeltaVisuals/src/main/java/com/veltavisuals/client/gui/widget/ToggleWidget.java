package com.veltavisuals.client.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * Простой переключатель "название — [ON/OFF]".
 * Клик по всей строке переключает значение и вызывает onChange.
 */
public class ToggleWidget extends ClickableWidget {

    private boolean value;
    private final Consumer<Boolean> onChange;

    // Цвета под тему "aurora" (мягкий сиреневый акцент)
    private static final int COLOR_TEXT = 0xFFE0E0FF;
    private static final int COLOR_ON = 0xFF7FB2FF;
    private static final int COLOR_OFF = 0xFF6B6B7A;
    private static final int COLOR_BG_HOVER = 0x33FFFFFF;

    public ToggleWidget(int x, int y, int width, int height,
                         String label, boolean initialValue,
                         Consumer<Boolean> onChange) {
        super(x, y, width, height, Text.literal(label));
        this.value = initialValue;
        this.onChange = onChange;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hovered = isMouseOver(mouseX, mouseY);

        if (hovered) {
            ctx.fill(getX(), getY(), getX() + width, getY() + height, COLOR_BG_HOVER);
        }

        // Название слева
        ctx.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                getMessage(),
                getX() + 4,
                getY() + (height - 8) / 2,
                COLOR_TEXT
        );

        // "Переключатель" справа: небольшой прямоугольник-индикатор + текст ON/OFF
        String stateText = value ? "ON" : "OFF";
        int stateColor = value ? COLOR_ON : COLOR_OFF;
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(stateText);
        int stateX = getX() + width - textWidth - 22;
        int stateY = getY() + (height - 8) / 2;

        // Индикатор-кружок/квадрат
        int dotSize = 8;
        int dotX = getX() + width - 14;
        int dotY = getY() + (height - dotSize) / 2;
        ctx.fill(dotX, dotY, dotX + dotSize, dotY + dotSize, stateColor);

        ctx.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                stateText,
                stateX,
                stateY,
                stateColor
        );
    }

    
}
