/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.ui.seeui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import one.pkg.libsl.api.ui.seeui.annotations.DisplayMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A configuration screen using standard Minecraft UI components.
 */
public class SeeUIConfigScreen extends Screen {
    private final Class<?> configClass;
    private final Runnable onSaved;
    private final String keyword;

    private final Screen lastScreen;
    private int currentY = 20;

    /**
     * Creates a new configuration screen.
     *
     * @param configClass The configuration class.
     * @param lastScreen  The previous screen.
     * @param onSaved     Callback to run when settings are saved.
     */
    public SeeUIConfigScreen(Class<?> configClass, Screen lastScreen, Runnable onSaved) {
        super(Component.translatable(getKeyword(configClass) + ".config.title"));
        this.lastScreen = lastScreen;
        this.configClass = configClass;
        this.onSaved = onSaved;
        this.keyword = getKeyword(configClass);
    }

    /**
     * Gets the translation keyword prefix for the configuration class.
     *
     * @param clazz The configuration class.
     * @return The keyword prefix.
     */
    private static String getKeyword(Class<?> clazz) {
        one.pkg.config.annotation.config.ConfigEntry ann = clazz.getAnnotation(one.pkg.config.annotation.config.ConfigEntry.class);
        return ann != null ? ann.value() : "gui.kreno";
    }

    @Override
    protected void init() {
        this.currentY = 20;
        parseConfig();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
            if (onSaved != null) onSaved.run();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    /**
     * Parses the configuration class and adds entries to the screen.
     */
    private void parseConfig() {
        Map<String, List<ConfigEntry>> categories = new LinkedHashMap<>();
        List<ConfigParserCache.ParsedField> parsedFields = ConfigParserCache.getParsedFields(configClass);
        for (ConfigParserCache.ParsedField pf : parsedFields) {
            ConfigEntry entry = createEntry(pf.field(), pf.group(), pf.key(), pf.comment(), pf.min(), pf.max(), pf.mode(), pf.displayMode());
            if (entry != null) categories.computeIfAbsent(pf.group(), k -> new ArrayList<>()).add(entry);
        }

        for (Map.Entry<String, List<ConfigEntry>> category : categories.entrySet()) {
            currentY += 24;
            List<ConfigEntry> entries = category.getValue();
            for (int i = 0; i < entries.size(); i += 2) {
                ConfigEntry entry1 = entries.get(i);
                AbstractWidget widget1 = createWidgetWithAutoSave(entry1);
                widget1.setX(this.width / 2 - 155);
                widget1.setY(currentY);
                this.addRenderableWidget(widget1);
                if (i + 1 < entries.size()) {
                    ConfigEntry entry2 = entries.get(i + 1);
                    AbstractWidget widget2 = createWidgetWithAutoSave(entry2);
                    widget2.setX(this.width / 2 + 5);
                    widget2.setY(currentY);
                    this.addRenderableWidget(widget2);
                }
                currentY += 24;
            }
        }
    }

    /**
     * Creates a widget that automatically saves its value when changed.
     *
     * @param entry The configuration entry.
     * @return The created widget.
     */
    private AbstractWidget createWidgetWithAutoSave(ConfigEntry entry) {
        AbstractWidget widget = entry.createWidget(0, 0, 150, 20);
        Component tooltip = entry.getTooltip();
        if (tooltip != null) {
            widget.setTooltip(Tooltip.create(tooltip));
        }
        return widget;
    }

    /**
     * Creates a configuration entry based on field type and annotations.
     *
     * @param field       The field.
     * @param category    The category.
     * @param key         The key.
     * @param comment     The comment.
     * @param min         The minimum value.
     * @param max         The maximum value.
     * @param mode        The entry mode.
     * @param displayMode The display mode annotation.
     * @return The created configuration entry.
     */
    private ConfigEntry createEntry(Field field, String category, String key, String comment, double min, double max, EntryMode mode, DisplayMode displayMode) {
        Class<?> type = field.getType();
        if ((mode == EntryMode.CYCLE || mode == EntryMode.BUTTON_GROUP) && displayMode != null)
            return new ConfigEntry.CycleEntry(field, category, key, comment, keyword, displayMode.cycleValues());
        if (mode == EntryMode.SLIDER)
            return new ConfigEntry.SliderEntry(field, category, key, comment, keyword, min, max);

        if (type == boolean.class || type == Boolean.class)
            return new ConfigEntry.BooleanEntry(field, category, key, comment, keyword);
        if (type == int.class || type == Integer.class)
            return new ConfigEntry.IntegerEntry(field, category, key, comment, keyword, min, max);
        if (type == long.class || type == Long.class)
            return new ConfigEntry.LongEntry(field, category, key, comment, keyword, min, max);
        if (type == double.class || type == Double.class || type == float.class || type == Float.class)
            return new ConfigEntry.DoubleEntry(field, category, key, comment, keyword, min, max);
        return null;
    }
}
