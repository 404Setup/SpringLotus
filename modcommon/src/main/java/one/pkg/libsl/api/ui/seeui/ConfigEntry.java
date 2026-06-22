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

import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Base class for configuration entries that can be displayed and edited in the UI.
 */
public abstract class ConfigEntry {
    protected static final Pattern TRAILING_ZEROS = Pattern.compile("\\.?0+$");
    protected final Field field;
    protected final String category;
    protected final String key;
    protected final String comment;
    protected final String keyword;

    /**
     * Constructs a new ConfigEntry.
     *
     * @param field    the field associated with this entry
     * @param category the category this entry belongs to
     * @param key      the unique key for this entry
     * @param comment  the comment/description for this entry
     * @param keyword  the mod's keyword/prefix used for translations
     */
    public ConfigEntry(Field field, String category, String key, String comment, String keyword) {
        this.field = field;
        this.category = category;
        this.key = key;
        this.comment = comment;
        this.keyword = keyword;
        this.field.setAccessible(true);
    }

    /**
     * Creates a Minecraft UI widget for editing this configuration entry.
     *
     * @param x      the x position of the widget
     * @param y      the y position of the widget
     * @param width  the width of the widget
     * @param height the height of the widget
     * @return the created widget
     */
    public abstract AbstractWidget createWidget(int x, int y, int width, int height);

    /**
     * Saves the current value of the widget back to the associated field.
     */
    public abstract void save();

    /**
     * Returns the translated label for this configuration entry.
     *
     * @return the label component
     */
    public Component getLabel() {
        return Component.translatable(keyword + ".config.entry." + category + "." + key);
    }

    /**
     * Returns the category of this entry.
     *
     * @return the category name
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the tooltip for this configuration entry, either from translations or the comment.
     *
     * @return the tooltip component, or null if no description is available
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
     * A configuration entry for boolean values, represented as a toggle button.
     */
    public static class BooleanEntry extends ConfigEntry {
        private boolean value;

        /**
         * Constructs a new BooleanEntry.
         *
         * @param field    the boolean field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
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
            return CycleButton.onOffBuilder(value)
                    .create(x, y, width, height, getLabel(), (_, newValue) -> {
                        this.value = newValue;
                        this.save();
                    });
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
     * A configuration entry for integer values, represented as a text input field with range validation.
     */
    public static class IntegerEntry extends ConfigEntry {
        private final double min;
        private final double max;
        private int value;

        /**
         * Constructs a new IntegerEntry.
         *
         * @param field    the integer field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
         * @param min      the minimum allowed value
         * @param max      the maximum allowed value
         */
        public IntegerEntry(Field field, String category, String key, String comment, String keyword, double min, double max) {
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
            EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, height, getLabel()) {
                private final int labelWidth = Minecraft.getInstance().font.width(getMessage());
                private final Component activeLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFFFF));
                private final Component inactiveLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFF48494A));

                @Override
                public void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                    int x = getX();
                    int y = getY();
                    int width = getWidth();
                    int height = getHeight();


                    int padding = 4;

                    extractor.textRendererForWidget(this, net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(net.minecraft.client.gui.TextAlignment.LEFT, x, y + (height - 8) / 2, active ? activeLabel : inactiveLabel);

                    int boxX = x + labelWidth + padding;
                    int boxWidth = Math.max(10, width - labelWidth - padding);

                    this.setX(boxX);
                    this.setWidth(boxWidth);
                    super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);
                    this.setX(x);
                    this.setWidth(width);
                }
            };
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
     * A configuration entry for long values, represented as a text input field with range validation.
     */
    public static class LongEntry extends ConfigEntry {
        private final double min;
        private final double max;
        private long value;

        /**
         * Constructs a new LongEntry.
         *
         * @param field    the long field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
         * @param min      the minimum allowed value
         * @param max      the maximum allowed value
         */
        public LongEntry(Field field, String category, String key, String comment, String keyword, double min, double max) {
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
            EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, height, getLabel()) {
                private final int labelWidth = Minecraft.getInstance().font.width(getMessage());
                private final Component activeLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFFFF));
                private final Component inactiveLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFF48494A));

                @Override
                public void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                    int x = getX();
                    int y = getY();
                    int width = getWidth();
                    int height = getHeight();


                    int padding = 4;

                    extractor.textRendererForWidget(this, net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(net.minecraft.client.gui.TextAlignment.LEFT, x, y + (height - 8) / 2, active ? activeLabel : inactiveLabel);

                    int boxX = x + labelWidth + padding;
                    int boxWidth = Math.max(10, width - labelWidth - padding);

                    this.setX(boxX);
                    this.setWidth(boxWidth);
                    super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);
                    this.setX(x);
                    this.setWidth(width);
                }
            };
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
     * A configuration entry for double/float values, represented as a text input field with range validation.
     */
    public static class DoubleEntry extends ConfigEntry {
        private final double min;
        private final double max;
        private double value;

        /**
         * Constructs a new DoubleEntry.
         *
         * @param field    the double or float field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
         * @param min      the minimum allowed value
         * @param max      the maximum allowed value
         */
        public DoubleEntry(Field field, String category, String key, String comment, String keyword, double min, double max) {
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
            EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, height, getLabel()) {
                private final int labelWidth = Minecraft.getInstance().font.width(getMessage());
                private final Component activeLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFFFF));
                private final Component inactiveLabel = getMessage().copy().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFF48494A));

                @Override
                public void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                    int x = getX();
                    int y = getY();
                    int width = getWidth();
                    int height = getHeight();


                    int padding = 4;

                    extractor.textRendererForWidget(this, net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(net.minecraft.client.gui.TextAlignment.LEFT, x, y + (height - 8) / 2, active ? activeLabel : inactiveLabel);

                    int boxX = x + labelWidth + padding;
                    int boxWidth = Math.max(10, width - labelWidth - padding);

                    this.setX(boxX);
                    this.setWidth(boxWidth);
                    super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);
                    this.setX(x);
                    this.setWidth(width);
                }
            };
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
     * A configuration entry that cycles through a set of predefined options.
     */
    public static class CycleEntry extends ConfigEntry {
        private final String[] options;
        private String value;

        /**
         * Constructs a new CycleEntry.
         *
         * @param field    the field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
         * @param options  the set of predefined options
         */
        public CycleEntry(Field field, String category, String key, String comment, String keyword, String[] options) {
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
            java.util.List<String> optionKeys = new java.util.ArrayList<>();
            java.util.List<Component> optionComponents = new java.util.ArrayList<>();
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
            return CycleButton.<String>builder(val -> {
                        int idx = optionKeys.indexOf(val);
                        return idx >= 0 ? optionComponents.get(idx) : Component.literal(val);
                    }, () -> value)
                    .withValues(optionKeys)
                    .create(x, y, width, height, getLabel(), (_, newValue) -> {
                        this.value = newValue;
                        this.save();
                    });
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
     * A configuration entry that uses a slider to select a numerical value within a range.
     */
    public static class SliderEntry extends ConfigEntry {
        private final double min;
        private final double max;
        private final boolean isInteger;
        private double actualValue;

        /**
         * Constructs a new SliderEntry.
         *
         * @param field    the numerical field associated with this entry
         * @param category the category this entry belongs to
         * @param key      the unique key for this entry
         * @param comment  the comment/description for this entry
         * @param keyword  the mod's keyword/prefix used for translations
         * @param min      the minimum allowed value
         * @param max      the maximum allowed value
         */
        public SliderEntry(Field field, String category, String key, String comment, String keyword, double min, double max) {
            super(field, category, key, comment, keyword);
            this.min = min;
            this.max = max;
            Class<?> type = field.getType();
            this.isInteger = type == int.class || type == Integer.class || type == long.class || type == Long.class;
            try {
                Number num = (Number) field.get(null);
                this.actualValue = num.doubleValue();
            } catch (IllegalAccessException e) {
                this.actualValue = min;
            }
        }

        @Override
        public AbstractWidget createWidget(int x, int y, int width, int height) {
            return new AbstractSliderButton(x, y, width, height, getLabel(), (actualValue - min) / (max - min)) {
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
                    setMessage(getLabel().copy().append(": ").append(valStr));
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

                @Override
                public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
                    super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);
                }

                @Override
                public void updateWidgetNarration(@NonNull NarrationElementOutput output) {
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
}
