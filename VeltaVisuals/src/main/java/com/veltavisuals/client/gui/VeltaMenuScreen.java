package com.veltavisuals.client.gui;

import com.veltavisuals.client.config.VeltaConfig;
import com.veltavisuals.client.gui.widget.ToggleWidget;
import com.veltavisuals.client.gui.widget.VeltaSliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Главный экран настроек VeltaVisuals — своё оригинальное клик-меню
 * (категории слева, панель настроек справа, плавные анимации открытия).
 * Дизайн вдохновлён современными clickgui-меню, но реализация полностью
 * своя: никакого копирования чужого кода/ассетов.
 */
public class VeltaMenuScreen extends Screen {

    private enum Category {
        HUD("HUD"),
        WORLD_AESTHETICS("Мир"),
        MENU_STYLE("Меню");

        final String label;
        Category(String label) { this.label = label; }
    }

    private Category selectedCategory = Category.HUD;

    // Анимация появления окна (0..1), считается в render() по времени
    private long openedAtMillis;
    private static final int FADE_IN_MS = 220;

    // Геометрия окна меню
    private int panelX, panelY, panelWidth, panelHeight;
    private static final int SIDEBAR_WIDTH = 90;

    public VeltaMenuScreen() {
        super(Text.literal("VeltaVisuals"));
    }

    @Override
    protected void init() {
        panelWidth = 360;
        panelHeight = 220;
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
        openedAtMillis = System.currentTimeMillis();

        rebuildWidgetsForCategory(selectedCategory);
    }

    /**
     * Пересобирает виджеты (кнопки-переключатели, слайдеры) под выбранную
     * категорию. Реальные ToggleWidget/SliderWidget — отдельные классы
     * в этом же пакете (gui.widget.*), здесь опущены для краткости.
     */
    private void rebuildWidgetsForCategory(Category category) {
        clearChildren();

        VeltaConfig cfg = VeltaConfig.get();
        int contentX = panelX + SIDEBAR_WIDTH + 16;
        int contentY = panelY + 16;
        int rowHeight = 22;

        switch (category) {
            case HUD -> {
                addToggleRow(contentX, contentY, "Координаты", cfg.showCoordinates,
                        v -> cfg.showCoordinates = v);
                addToggleRow(contentX, contentY + rowHeight, "FPS", cfg.showFps,
                        v -> cfg.showFps = v);
                addToggleRow(contentX, contentY + rowHeight * 2, "Часы", cfg.showClock,
                        v -> cfg.showClock = v);
                addToggleRow(contentX, contentY + rowHeight * 3, "Компас", cfg.showCompass,
                        v -> cfg.showCompass = v);
            }
            case WORLD_AESTHETICS -> {
                addToggleRow(contentX, contentY, "Частицы окружения",
                        cfg.ambientParticlesEnabled, v -> cfg.ambientParticlesEnabled = v);
                addToggleRow(contentX, contentY + rowHeight, "Шейдер погоды",
                        cfg.customWeatherShaderEnabled, v -> cfg.customWeatherShaderEnabled = v);
                addToggleRow(contentX, contentY + rowHeight * 2, "Динамическое небо",
                        cfg.dynamicSkyTintEnabled, v -> cfg.dynamicSkyTintEnabled = v);
            }
            case MENU_STYLE -> {
                // Слайдер скорости анимации, выбор темы (aurora/nightfall/...)
                addThemeSelector(contentX, contentY, cfg);
                addSpeedSlider(contentX, contentY + rowHeight, cfg);
            }
        }
    }

    private static final int ROW_WIDTH = 240;
    private static final int ROW_HEIGHT = 18;

    private void addToggleRow(int x, int y, String label, boolean value,
                               java.util.function.Consumer<Boolean> onChange) {
        addDrawableChild(new ToggleWidget(x, y, ROW_WIDTH, ROW_HEIGHT, label, value, onChange));
    }

    // Доступные темы меню — по кругу переключаются кликом
    private static final String[] THEMES = {"aurora", "nightfall", "monochrome"};

    private void addThemeSelector(int x, int y, VeltaConfig cfg) {
        int currentIndex = java.util.Arrays.asList(THEMES).indexOf(cfg.menuTheme);
        if (currentIndex < 0) currentIndex = 0;
        final int[] indexHolder = {currentIndex};

        // Тема переключается обычной кнопкой-циклом: используем ToggleWidget
        // как "кликабельную строку", но текст меняем вручную через label-обёртку.
        addDrawableChild(new net.minecraft.client.gui.widget.ButtonWidget.Builder(
                Text.literal("Тема: " + cfg.menuTheme),
                button -> {
                    indexHolder[0] = (indexHolder[0] + 1) % THEMES.length;
                    cfg.menuTheme = THEMES[indexHolder[0]];
                    button.setMessage(Text.literal("Тема: " + cfg.menuTheme));
                }
        ).dimensions(x, y, ROW_WIDTH, ROW_HEIGHT).build());
    }

    private void addSpeedSlider(int x, int y, VeltaConfig cfg) {
        addDrawableChild(new VeltaSliderWidget(
                x, y, ROW_WIDTH, ROW_HEIGHT,
                "Скорость анимации",
                0.5, 2.0,
                cfg.menuAnimationSpeed,
                newValue -> cfg.menuAnimationSpeed = newValue
        ));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float progress = Math.min(1f,
                (System.currentTimeMillis() - openedAtMillis) / (float) FADE_IN_MS);

        renderBackground(ctx, mouseX, mouseY, delta); // лёгкое затемнение игры
        drawAnimatedPanel(ctx, progress);
        drawSidebar(ctx);
        drawContentHeader(ctx);

        super.render(ctx, mouseX, mouseY, delta);
    }

    /** Основная панель с fade-анимацией появления и мягким фоном. */
    private void drawAnimatedPanel(DrawContext ctx, float progress) {
        int alpha = (int) (progress * 235);
        int bgColor = (alpha << 24) | 0x1A1A22;
        int borderColor = (alpha << 24) | 0x7FB2FF;

        // Тело окна
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, bgColor);
        // Тонкая рамка
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + 1, borderColor);
        ctx.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, borderColor);
        ctx.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor);
        ctx.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, borderColor);
    }

    private void drawSidebar(DrawContext ctx) {
        for (Category category : Category.values()) {
            int index = category.ordinal();
            int rowY = panelY + 16 + index * 24;
            boolean selected = category == selectedCategory;

            if (selected) {
                ctx.fill(panelX + 4, rowY, panelX + SIDEBAR_WIDTH, rowY + 20, 0x557FB2FF);
            }

            ctx.drawTextWithShadow(
                    textRenderer,
                    category.label,
                    panelX + 12,
                    rowY + 6,
                    selected ? 0xFFFFFFFF : 0xFFAAAABE
            );
        }

        // Разделитель между сайдбаром и контентом
        ctx.fill(panelX + SIDEBAR_WIDTH, panelY + 8, panelX + SIDEBAR_WIDTH + 1, panelY + panelHeight - 8, 0x33FFFFFF);
    }

    private void drawContentHeader(DrawContext ctx) {
        ctx.drawTextWithShadow(textRenderer, "VeltaVisuals — " + selectedCategory.label,
                panelX + SIDEBAR_WIDTH + 16, panelY + 4, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Category category : Category.values()) {
            if (isSidebarRowHovered(category, mouseX, mouseY)) {
                selectedCategory = category;
                rebuildWidgetsForCategory(category);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isSidebarRowHovered(Category category, double mouseX, double mouseY) {
        int index = category.ordinal();
        int rowY = panelY + 16 + index * 24;
        return mouseX >= panelX + 8 && mouseX <= panelX + SIDEBAR_WIDTH
                && mouseY >= rowY && mouseY <= rowY + 20;
    }

    @Override
    public void close() {
        VeltaConfig.save();
        super.close();
    }

    @Override
    public boolean shouldPauseGame() {
        return false; // визуальный мод не должен ставить игру на паузу
    }
}
