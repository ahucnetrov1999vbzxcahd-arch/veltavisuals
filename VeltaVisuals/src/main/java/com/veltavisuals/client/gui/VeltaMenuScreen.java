package com.veltavisuals.client.gui;

import com.veltavisuals.client.config.VeltaConfig;
import com.veltavisuals.client.gui.widget.ToggleWidget;
import com.veltavisuals.client.gui.widget.VeltaSliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Главный экран настроек VeltaVisuals — своё оригинальное клик-меню
 * (категории слева, панель настроек справа, плавные анимации открытия).
 * Дизайн вдохновлён современными clickgui-меню, но реализация полностью
 * своя: никакого копирования чужого кода/ассетов.
 *
 * Все интерактивные элементы (вкладки сайдбара, тогглы, слайдер) — это
 * обычные виджеты Minecraft, добавленные через addDrawableChild(...).
 * Это специально сделано вместо ручного оверрайда mouseClicked(...),
 * т.к. сигнатура этого метода в Screen может отличаться между версиями
 * Minecraft — виджеты сами обрабатывают клики стабильным образом.
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

    private long openedAtMillis;
    private static final int FADE_IN_MS = 220;

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

    private void rebuildWidgetsForCategory(Category category) {
        clearChildren();

        addSidebarButtons();

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
                addThemeSelector(contentX, contentY, cfg);
                addSpeedSlider(contentX, contentY + rowHeight, cfg);
            }
        }
    }

    private void addSidebarButtons() {
        for (Category category : Category.values()) {
            int index = category.ordinal();
            int rowY = panelY + 16 + index * 24;

            Text label = category == selectedCategory
                    ? Text.literal("» " + category.label)
                    : Text.literal(category.label);

            addDrawableChild(ButtonWidget.builder(label, button -> {
                        selectedCategory = category;
                        rebuildWidgetsForCategory(category);
                    })
                    .dimensions(panelX + 4, rowY, SIDEBAR_WIDTH - 8, 20)
                    .build());
        }
    }

    private static final int ROW_WIDTH = 240;
    private static final int ROW_HEIGHT = 18;

    private void addToggleRow(int x, int y, String label, boolean value,
                               java.util.function.Consumer<Boolean> onChange) {
        addDrawableChild(new ToggleWidget(x, y, ROW_WIDTH, ROW_HEIGHT, label, value, onChange));
    }

    private static final String[] THEMES = {"aurora", "nightfall", "monochrome"};

    private void addThemeSelector(int x, int y, VeltaConfig cfg) {
        int currentIndex = java.util.Arrays.asList(THEMES).indexOf(cfg.menuTheme);
        if (currentIndex < 0) currentIndex = 0;
        final int[] indexHolder = {currentIndex};

        addDrawableChild(ButtonWidget.builder(
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

        renderBackground(ctx, mouseX, mouseY, delta);
        drawAnimatedPanel(ctx, progress);
        drawContentHeader(ctx);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawAnimatedPanel(DrawContext ctx, float progress) {
        int alpha = (int) (progress * 235);
        int bgColor = (alpha << 24) | 0x1A1A22;
        int borderColor = (alpha << 24) | 0x7FB2FF;

        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, bgColor);
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + 1, borderColor);
        ctx.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, borderColor);
        ctx.fill(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor);
        ctx.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, borderColor);

        ctx.fill(panelX + SIDEBAR_WIDTH, panelY + 8, panelX + SIDEBAR_WIDTH + 1, panelY + panelHeight - 8, 0x33FFFFFF);
    }

    private void drawContentHeader(DrawContext ctx) {
        ctx.drawTextWithShadow(textRenderer, "VeltaVisuals — " + selectedCategory.label,
                panelX + SIDEBAR_WIDTH + 16, panelY + 4, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        VeltaConfig.save();
        super.close();
    }
}
