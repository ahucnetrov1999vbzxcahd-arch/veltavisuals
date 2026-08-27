package com.veltavisuals.client.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * Слайдер для float-значений (например, скорость анимации меню 0.5x..2.0x).
 * Наследуется от ванильного SliderWidget — вся логика перетаскивания
 * мышью уже реализована в родительском классе, здесь только
 * маппинг value (0..1) в реальный диапазон и подпись.
 */
public class VeltaSliderWidget extends SliderWidget {

    private final double min;
    private final double max;
    private final String label;
    private final Consumer<Float> onChange;
    private final boolean isPercentStyle; // true -> показывать "x1.25", false -> просто число

    public VeltaSliderWidget(int x, int y, int width, int height,
                              String label, double min, double max,
                              float initialValue, Consumer<Float> onChange) {
        this(x, y, width, height, label, min, max, initialValue, onChange, true);
    }

    public VeltaSliderWidget(int x, int y, int width, int height,
                              String label, double min, double max,
                              float initialValue, Consumer<Float> onChange,
                              boolean isPercentStyle) {
        // SliderWidget хранит value внутри как 0..1 (this.value), поэтому
        // сразу нормализуем стартовое значение
        super(x, y, width, height, Text.literal(""), normalize(initialValue, min, max));
        this.min = min;
        this.max = max;
        this.label = label;
        this.onChange = onChange;
        this.isPercentStyle = isPercentStyle;
        updateMessage();
    }

    private static double normalize(float real, double min, double max) {
        return (real - min) / (max - min);
    }

    private double denormalize() {
        return min + (max - min) * this.value;
    }

    @Override
    protected void updateMessage() {
        double real = denormalize();
        String valueText = isPercentStyle
                ? String.format("x%.2f", real)
                : String.format("%.2f", real);
        setMessage(Text.literal(label + ": " + valueText));
    }

    @Override
    protected void applyValue() {
        onChange.accept((float) denormalize());
    }

    @Override
    public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Фон дорожки слайдера
        ctx.fill(getX(), getY(), getX() + width, getY() + height, 0x55000000);

        // Заполненная часть (прогресс) под тему "aurora"
        int filledWidth = (int) (width * this.value);
        ctx.fill(getX(), getY(), getX() + filledWidth, getY() + height, 0xFF7FB2FF);

        // Ползунок (тонкая полоска на границе прогресса)
        int handleX = getX() + filledWidth - 1;
        ctx.fill(handleX, getY() - 1, handleX + 3, getY() + height + 1, 0xFFFFFFFF);

        // Текст поверх (название + значение)
        ctx.drawCenteredTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                getMessage(),
                getX() + width / 2,
                getY() + (height - 8) / 2,
                0xFFFFFFFF
        );
    }
}
