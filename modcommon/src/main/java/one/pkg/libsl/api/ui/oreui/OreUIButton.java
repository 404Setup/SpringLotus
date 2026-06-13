/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.ui.oreui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * A native Minecraft button component styled to look like an OreUI button.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUIButton button = OreUIButton.oreUIBuilder(Component.literal("Click Me"), b -> {
 *     System.out.println("Clicked!");
 * }).pos(10, 10).size(150, 40).style(OreUIButton.Style.GREEN).build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUIButton extends Button {

    private Style style;
    private float clickProgress = 0.0f;
    private Component cachedEnabledMessage;
    private Component cachedDisabledMessage;
    private String cachedFullString;

    /**
     * Constructs a new {@code OreUIButton}.
     *
     * @param x               The x-position.
     * @param y               The y-position.
     * @param width           The button width.
     * @param height          The button height.
     * @param message         The button text.
     * @param onPress         The action to perform when pressed.
     * @param createNarration The narration generator.
     * @param style           The button style.
     */
    protected OreUIButton(int x, int y, int width, int height,
                          Component message, OnPress onPress,
                          CreateNarration createNarration, Style style) {
        super(x, y, width, height, message, onPress, createNarration);
        this.style = style;
        updateCachedMessages();
    }

    /**
     * Creates a new builder for an {@code OreUIButton}.
     *
     * @param message The button text.
     * @param onPress The action to perform when pressed.
     * @return A new {@link OreUIBuilder} instance.
     */
    public static OreUIBuilder oreUIBuilder(Component message, OnPress onPress) {
        return new OreUIBuilder(message, onPress);
    }

    private void updateCachedMessages() {
        Component msg = getMessage();
        boolean isBold = style == Style.NORMAL || style == Style.GREEN;
        this.cachedDisabledMessage = msg.copy().withStyle(
                net.minecraft.network.chat.Style.EMPTY.withColor(0xFF48494A).withBold(isBold)
        );
        this.cachedEnabledMessage = msg.copy().withStyle(
                net.minecraft.network.chat.Style.EMPTY.withColor(0xFFFFFFFF).withBold(isBold)
        );
        this.cachedFullString = msg.getString();
    }

    @Override
    public void setMessage(@NotNull Component message) {
        super.setMessage(message);
        updateCachedMessages();
    }

    /**
     * Sets the style of this button.
     *
     * @param style The new {@link Style}.
     */
    public void setStyle(Style style) {
        if (this.style != style) {
            this.style = style;
            updateCachedMessages();
        }
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor extractor,
                                   int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        boolean disabled = !active;
        boolean hoveredOrActive = isHoveredOrFocused() || isHovered();
        boolean isPressed = !disabled && hoveredOrActive && Minecraft.getInstance().mouseHandler.isLeftPressed();

        float targetClick = isPressed ? 1.0f : 0.0f;
        this.clickProgress = Mth.lerp(0.4f, this.clickProgress, targetClick);

        int dx = (int) (width * 0.02f * this.clickProgress);
        int dy = (int) (height * 0.02f * this.clickProgress);

        int rX = x + dx;
        int rY = y + dy;
        int rW = width - dx * 2;
        int rH = height - dy * 2;

        int bgColor = 0;
        int borderColor = 0xFF1E1E1F;

        if (disabled) {
            bgColor = 0xFFD0D1D4;
            borderColor = 0xFF8C8D90;
        } else if (style == Style.GREEN) {
            bgColor = hoveredOrActive ? 0xFF4A9E32 : 0xFF3C8527;
        } else if (style != Style.CATEGORY_SELECTED && style != Style.CATEGORY_UNSELECTED) {
            bgColor = hoveredOrActive ? 0xFFB1B2B5 : 0xFFD0D1D4;
        }

        if (style == Style.CATEGORY_SELECTED || style == Style.CATEGORY_UNSELECTED) {
            extractor.fill(x, y, x + width, y + height, hoveredOrActive ? 0x1AFFFFFF : 0x00000000);
            if (style == Style.CATEGORY_SELECTED) {
                extractor.fill(x, y + 4, x + 4, y + height - 4, 0xFF4A9E32);
            }
        } else {
            extractor.fill(rX, rY, rX + rW, rY + rH, borderColor);
            extractor.fill(rX + 2, rY + 2, rX + rW - 2, rY + rH - 2, bgColor);

            if (!disabled && style != Style.GREEN && style != Style.FLAT) {
                extractor.fill(rX + 2, rY + rH - 6, rX + rW - 2, rY + rH - 2, 0xFF58585A);
            }
        }

        Component coloredMessage = disabled ? cachedDisabledMessage : cachedEnabledMessage;

        Minecraft minecraft = Minecraft.getInstance();
        net.minecraft.client.gui.Font font = minecraft.font;
        int maxTextWidth = (style == Style.CATEGORY_SELECTED ||
                style == Style.CATEGORY_UNSELECTED) ? width - 16 : rW - 12;
        maxTextWidth = Math.max(0, maxTextWidth);
        Component renderedMessage = coloredMessage;
        int fullWidth = font.width(coloredMessage);
        if (fullWidth > maxTextWidth && maxTextWidth > 0) {
            String fullText = this.cachedFullString;
            if (!fullText.isEmpty()) {
                if (style == Style.CATEGORY_SELECTED || style == Style.CATEGORY_UNSELECTED) {
                    long time = System.currentTimeMillis() / 200;
                    int scrollAmount = (int) (time % (fullText.length() + 10));
                    if (scrollAmount > fullText.length()) {
                        scrollAmount = 0;
                    }
                    String scrolledText = fullText
                            .substring(scrollAmount) + "   " + fullText.substring(0, scrollAmount);
                    String trimmed = font.plainSubstrByWidth(scrolledText, Math.max(0, maxTextWidth));
                    renderedMessage = Component.literal(trimmed).withStyle(coloredMessage.getStyle());
                } else {
                    String trimmed = font.plainSubstrByWidth(fullText, Math.max(0, maxTextWidth - font.width("...")));
                    renderedMessage = Component.literal(trimmed + "...").withStyle(coloredMessage.getStyle());
                }
            }
        }

        ActiveTextCollector textCollector = extractor.textRendererForWidget(this,
                GuiGraphicsExtractor.HoveredTextEffects.NONE);
        if (style == Style.CATEGORY_SELECTED || style == Style.CATEGORY_UNSELECTED) {
            textCollector.accept(TextAlignment.LEFT, x + 10, y + (height - 8) / 2, renderedMessage);
        } else {
            textCollector.accept(TextAlignment.CENTER, x + width / 2,
                    y + (height - 8) / 2, renderedMessage);
        }
    }

    /**
     * Styles for the {@code OreUIButton}.
     */
    public enum Style {
        /**
         * Represents the default style for the {@code OreUIButton}.
         */
        NORMAL,
        /**
         * Represents the {@code GREEN} style for {@link OreUIButton}.
         * <p>
         * This style is characterized by a green background color
         * and black text. It is commonly used to indicate a positive or
         * success-related status in the UI.
         */
        GREEN,
        /**
         * A flat style for the {@code OreUIButton}.
         * Typically features minimalistic design with subdued visuals.
         */
        FLAT,
        /**
         * Represents a style for a button or tag indicating a selected category.
         * Typically used to visually differentiate a selected category from others.
         */
        CATEGORY_SELECTED,
        /**
         * Represents the unselected state for a category-style button in the {@code OreUIButton}.
         * This style is used when the category is not actively selected or highlighted.
         */
        CATEGORY_UNSELECTED
    }

    /**
     * A builder class for creating {@link OreUIButton} instances.
     */
    public static class OreUIBuilder {
        private final Component message;
        private final OnPress onPress;
        private int x = 0;
        private int y = 0;
        private int width = 150;
        private int height = 40;
        private Style style = Style.NORMAL;
        private Tooltip tooltip;

        /**
         * Constructs a new {@code OreUIBuilder}.
         *
         * @param message The button text.
         * @param onPress The action to perform when pressed.
         */
        public OreUIBuilder(Component message, OnPress onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        /**
         * Sets the position of the button.
         *
         * @param x The x-position.
         * @param y The y-position.
         * @return This builder instance.
         */
        public OreUIBuilder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the size of the button.
         *
         * @param width  The button width.
         * @param height The button height.
         * @return This builder instance.
         */
        public OreUIBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the style of the button.
         *
         * @param style The {@link Style}.
         * @return This builder instance.
         */
        public OreUIBuilder style(Style style) {
            this.style = style;
            return this;
        }

        /**
         * Sets the tooltip for the button.
         *
         * @param tooltip The {@link Tooltip}.
         * @return This builder instance.
         */
        public OreUIBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link OreUIButton} instance.
         *
         * @return A new {@link OreUIButton}.
         */
        public OreUIButton build() {
            OreUIButton button = new OreUIButton(x, y, width, height, message,
                    onPress, Button.DEFAULT_NARRATION, style);
            if (this.tooltip != null) {
                button.setTooltip(this.tooltip);
            }
            return button;
        }
    }
}
