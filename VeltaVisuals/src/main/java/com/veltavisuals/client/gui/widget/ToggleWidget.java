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
        public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
    }
}
   
