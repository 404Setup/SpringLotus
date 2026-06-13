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
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import one.pkg.libsl.api.ui.oreui.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static one.pkg.libsl.api.ui.seeui.ConfigEntry.TRAILING_ZEROS;

/**
 * Base class for OreUI configuration entries.
 */
@SuppressWarnings("unused")
public abstract class OreUIConfigEntry {
    protected final Field field;
    protected final String category;
    protected final String key;
    protected final String comment;
    protected final String keyword;

    /**
     * Creates a new configuration entry.
     *
     * @param field    The field this entry represents.
     * @param category The category this entry belongs to.
     * @param key      The configuration key.
     * @param comment  The comment/description for this entry.
     * @param keyword  The translation keyword prefix.
     */
    public OreUIConfigEntry(Field field, String category, String key, String comment, String keyword) {
        this.field = field;
        this.category = category;
        this.key = key;
        this.comment = comment;
        this.keyword = keyword;
        this.field.setAccessible(true);
    }

    /**
     * Creates the widget for this configuration entry.
     *
     * @param x      The x position.
     * @param y      The y position.
     * @param width  The width of the widget.
     * @param height The height of the widget.
     * @return The created widget.
     */
    public abstract AbstractWidget createWidget(int x, int y, int width, int height);

    /**
     * Saves the current value to the field.
     */
    public abstract void save();

    /**
     * Gets the label component for this entry.
     *
     * @return The label component.
     */
    public Component getLabel() {
        return Component.translatable(keyword + ".config.entry." + category + "." + key);
    }

    /**
     * Gets the category of this entry.
     *
     * @return The category name.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the tooltip component for this entry.
     *
     * @return The tooltip component, or null if not available.
     */
    public Component getTooltip() {
        String tooltipKey = keyword + ".config.entry." + category + "." + key + ".desc";
        if (Language.getInstance().has(tooltipKey)) {
            return Component.translatable(tooltipKey);
        }
        if (comment == null || comment.isEmpty()) return null;
        return Component.literal(comment);
    }

    /**
     * Configuration entry for boolean values.
     */
    public static class BooleanEntry extends OreUIConfigEntry {
        private boolean value;

        /**
         * Creates a new boolean configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         */
        public BooleanEntry(Field field, String category, String key, String comment, String keyword) {
            super(field, category, key, comment, keyword);
            try {
                Object val = field.get(null);
                this.value = val instanceof Boolean ? (Boolean) val : false;
            } catch (IllegalAccessException e) {
                this.value = false;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            return OreUISwitch.builder(Component.empty(), newValue -> {
                this.value = newValue;
                this.save();
            }).pos(x, y).initialState(value).build();
        }

        @Override
        public void save() {
            try {
                field.set(null, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry for integer values.
     */
    public static class IntegerEntry extends OreUIConfigEntry {
        private final double min;
        private final double max;
        private int value;

        /**
         * Creates a new integer configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param min      The minimum allowed value.
         * @param max      The maximum allowed value.
         */
        public IntegerEntry(Field field, String category, String key, String comment,
                            String keyword, double min, double max) {
            super(field, category, key, comment, keyword);
            this.min = min;
            this.max = max;
            try {
                Object val = field.get(null);
                this.value = val instanceof Number ? ((Number) val).intValue() : 0;
            } catch (IllegalAccessException e) {
                this.value = 0;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            OreUITextField editBox = OreUITextField.builder(Minecraft.getInstance().font,
                    Component.empty()).pos(x, y).size(width, height).build();
            editBox.setValue(String.valueOf(value));
            editBox.setResponder(s -> {
                if (s.isEmpty() || s.equals("-")) return;
                try {
                    int val = Integer.parseInt(s);
                    if (val >= min && val <= max) {
                        this.value = val;
                        this.save();
                    }
                } catch (NumberFormatException ignored) {
                }
            });
            return editBox;
        }

        @Override
        public void save() {
            try {
                if (field.getType() == int.class) field.setInt(null, value);
                else field.set(null, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry for long values.
     */
    public static class LongEntry extends OreUIConfigEntry {
        private final double min;
        private final double max;
        private long value;

        /**
         * Creates a new long configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param min      The minimum allowed value.
         * @param max      The maximum allowed value.
         */
        public LongEntry(Field field, String category, String key, String comment,
                         String keyword, double min, double max) {
            super(field, category, key, comment, keyword);
            this.min = min;
            this.max = max;
            try {
                Object val = field.get(null);
                this.value = val instanceof Number ? ((Number) val).longValue() : 0L;
            } catch (IllegalAccessException e) {
                this.value = 0L;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            OreUITextField editBox = OreUITextField.builder(Minecraft.getInstance().font,
                    Component.empty()).pos(x, y).size(width, height).build();
            editBox.setValue(String.valueOf(value));
            editBox.setResponder(s -> {
                if (s.isEmpty() || s.equals("-")) return;
                try {
                    long val = Long.parseLong(s);
                    if (val >= min && val <= max) {
                        this.value = val;
                        this.save();
                    }
                } catch (NumberFormatException ignored) {
                }
            });
            return editBox;
        }

        @Override
        public void save() {
            try {
                if (field.getType() == long.class) field.setLong(null, value);
                else field.set(null, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry for double values.
     */
    public static class DoubleEntry extends OreUIConfigEntry {
        private final double min;
        private final double max;
        private double value;

        /**
         * Creates a new double configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param min      The minimum allowed value.
         * @param max      The maximum allowed value.
         */
        public DoubleEntry(Field field, String category, String key, String comment,
                           String keyword, double min, double max) {
            super(field, category, key, comment, keyword);
            this.min = min;
            this.max = max;
            try {
                Object val = field.get(null);
                this.value = val instanceof Number ? ((Number) val).doubleValue() : 0.0;
            } catch (IllegalAccessException e) {
                this.value = 0.0;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            OreUITextField editBox = OreUITextField.builder(Minecraft.getInstance().font, Component.empty())
                    .pos(x, y).size(width, height).build();
            editBox.setValue(String.valueOf(value));
            editBox.setResponder(s -> {
                if (s.isEmpty() || s.equals("-") || s.equals(".")) return;
                try {
                    double val = Double.parseDouble(s);
                    if (val >= min && val <= max) {
                        this.value = val;
                        this.save();
                    }
                } catch (NumberFormatException ignored) {
                }
            });
            return editBox;
        }

        @Override
        public void save() {
            try {
                if (field.getType() == double.class) field.setDouble(null, value);
                else if (field.getType() == float.class) field.setFloat(null, (float) value);
                else field.set(null, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry for cycling through options.
     */
    public static class CycleEntry extends OreUIConfigEntry {
        private final String[] options;
        private String value;

        /**
         * Creates a new cycle configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param options  The available options.
         */
        public CycleEntry(Field field, String category, String key, String comment,
                          String keyword, String[] options) {
            super(field, category, key, comment, keyword);
            this.options = options;
            try {
                Object obj = field.get(null);
                this.value = obj != null ? String.valueOf(obj) : options[0];
            } catch (IllegalAccessException e) {
                this.value = options[0];
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            List<String> optionKeys = new ArrayList<>();
            List<Component> optionComponents = new ArrayList<>();
            for (String opt : options) {
                if (opt.contains(":")) {
                    String[] split = opt.split(":", 2);
                    optionKeys.add(split[0]);
                    optionComponents.add(Component.literal(split[1]));
                } else {
                    optionKeys.add(opt);
                    optionComponents.add(Component.literal(opt));
                }
            }
            int initialIndex = optionKeys.indexOf(value);
            OreUIDropdown dropdown = OreUIDropdown.builder(Component.empty()).pos(x, y)
                    .size(width, height).options(optionComponents).onSelect(idx -> {
                        this.value = optionKeys.get(idx);
                        this.save();
                    }).build();
            if (initialIndex != -1) {
                dropdown.setSelectedIndex(initialIndex);
            }
            return dropdown;
        }

        @Override
        public void save() {
            try {
                Class<?> type = field.getType();
                if (type == String.class) {
                    field.set(null, value);
                } else if (type.isEnum()) {
                    try {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Object enumVal = Enum.valueOf((Class) type, value);
                        field.set(null, enumVal);
                    } catch (IllegalArgumentException ex) {
                        for (Object enumConstant : type.getEnumConstants()) {
                            if (enumConstant.toString().equals(value)) {
                                field.set(null, enumConstant);
                                break;
                            }
                        }
                    }
                } else if (type == int.class || type == Integer.class) {
                    int val = Integer.parseInt(value);
                    if (type == int.class) field.setInt(null, val);
                    else field.set(null, val);
                }
            } catch (IllegalAccessException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry using a slider.
     */
    public static class SliderEntry extends OreUIConfigEntry {
        private final double min;
        private final double max;
        private final boolean isInteger;
        private double actualValue;

        /**
         * Creates a new slider configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param min      The minimum allowed value.
         * @param max      The maximum allowed value.
         */
        public SliderEntry(Field field, String category, String key, String comment,
                           String keyword, double min, double max) {
            super(field, category, key, comment, keyword);
            this.min = min;
            this.max = max;
            Class<?> type = field.getType();
            this.isInteger = type == int.class || type == Integer.class ||
                    type == long.class || type == Long.class;
            try {
                Number num = (Number) field.get(null);
                this.actualValue = num.doubleValue();
            } catch (IllegalAccessException e) {
                this.actualValue = min;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            return new OreUISlider(x, y, width, height, Component.empty(),
                    (actualValue - min) / (max - min)) {
                {
                    updateMessage();
                }

                @Override
                protected void updateMessage() {
                    String valStr;
                    if (isInteger) {
                        valStr = String.valueOf((long) actualValue);
                    } else {
                        valStr = TRAILING_ZEROS.matcher(String.format("%.8f", actualValue)).replaceAll("");
                    }
                    setMessage(Component.literal(valStr));
                }

                @Override
                protected void applyValue() {
                    double newValue = min + (max - min) * this.value;
                    if (isInteger) {
                        actualValue = Math.round(newValue);
                        this.value = (actualValue - min) / (max - min);
                    } else {
                        double factor = 1e8;
                        actualValue = Math.round(newValue * factor) / factor;
                    }
                    updateMessage();
                    save();
                }
            };
        }

        @Override
        public void save() {
            try {
                if (field.getType() == int.class) field.setInt(null, (int) actualValue);
                else if (field.getType() == long.class) field.setLong(null, (long) actualValue);
                else if (field.getType() == double.class) field.setDouble(null, actualValue);
                else if (field.getType() == float.class) field.setFloat(null, (float) actualValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configuration entry using a button group.
     */
    public static class ButtonGroupEntry extends OreUIConfigEntry {
        private final String[] options;
        private String value;

        /**
         * Creates a new button group configuration entry.
         *
         * @param field    The field this entry represents.
         * @param category The category this entry belongs to.
         * @param key      The configuration key.
         * @param comment  The comment/description for this entry.
         * @param keyword  The translation keyword prefix.
         * @param options  The available options.
         */
        public ButtonGroupEntry(Field field, String category, String key, String comment,
                                String keyword, String[] options) {
            super(field, category, key, comment, keyword);
            this.options = options;
            try {
                Object obj = field.get(null);
                this.value = obj != null ? String.valueOf(obj) : options[0];
            } catch (IllegalAccessException e) {
                this.value = options[0];
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            List<String> optionKeys = new ArrayList<>();
            List<Component> optionComponents = new ArrayList<>();
            for (String opt : options) {
                if (opt.contains(":")) {
                    String[] split = opt.split(":", 2);
                    optionKeys.add(split[0]);
                    optionComponents.add(Component.literal(split[1]));
                } else {
                    optionKeys.add(opt);
                    optionComponents.add(Component.literal(opt));
                }
            }
            int initialIndex = optionKeys.indexOf(value);
            return OreUIButtonGroup.builder().pos(x, y).size(width, height)
                    .options(optionComponents).selectedIndex(initialIndex == -1 ? 0 : initialIndex)
                    .onSelect(idx -> {
                        this.value = optionKeys.get(idx);
                        this.save();
                    }).build();
        }

        @Override
        public void save() {
            try {
                Class<?> type = field.getType();
                if (type == String.class) {
                    field.set(null, value);
                } else if (type.isEnum()) {
                    try {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Object enumVal = Enum.valueOf((Class) type, value);
                        field.set(null, enumVal);
                    } catch (IllegalArgumentException ex) {
                        for (Object enumConstant : type.getEnumConstants()) {
                            if (enumConstant.toString().equals(value)) {
                                field.set(null, enumConstant);
                                break;
                            }
                        }
                    }
                } else if (type == int.class || type == Integer.class) {
                    int val = Integer.parseInt(value);
                    if (type == int.class) field.setInt(null, val);
                    else field.set(null, val);
                }
            } catch (IllegalAccessException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
