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

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

/**
 * A native Minecraft slider component styled to look like an OreUI slider.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUISlider slider = OreUISlider.builder(Component.literal("Volume"))
 *     .pos(10, 10).size(200, 30).value(0.5).tooltip(Tooltip.create(Component.literal("Tooltip"))).build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUISlider extends AbstractSliderButton {

    private double displayedValue = -1.0;
    private Component cachedMessageActive;
    private Component cachedMessageInactive;

    /**
     * Constructs a new {@code OreUISlider}.
     *
     * @param x       The x-position.
     * @param y       The y-position.
     * @param width   The width.
     * @param height  The height.
     * @param message The message to display.
     * @param value   The initial value.
     */
    public OreUISlider(int x, int y, int width, int height, Component message, double value) {
        super(x, y, width, height, message, value);
        updateCachedMessages();
    }

    /**
     * Creates a new builder for an {@code OreUISlider}.
     *
     * @param message The message to display.
     * @return A new {@link OreUIBuilder} instance.
     */
    public static OreUIBuilder builder(Component message) {
        return new OreUIBuilder(message);
    }

    @Override
    public void setMessage(Component message) {
        super.setMessage(message);
        updateCachedMessages();
    }

    private void updateCachedMessages() {
        Component msg = getMessage();
        this.cachedMessageActive = msg.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
        this.cachedMessageInactive = msg.copy().withStyle(Style.EMPTY.withColor(0xFF48494A));
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
    }

    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                         int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        boolean disabled = !active;
        boolean hovered = isHoveredOrFocused() || isHovered();

        if (this.displayedValue == -1.0) {
            this.displayedValue = this.value;
        }
        this.displayedValue = net.minecraft.util.Mth.lerp(0.4, this.displayedValue, this.value);
        if (Math.abs(this.displayedValue - this.value) < 0.01) {
            this.displayedValue = this.value;
        }

        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                .accept(TextAlignment.RIGHT, x + width, y,
                        disabled ? cachedMessageInactive : cachedMessageActive);

        int trackHeight = 8;
        int trackY = y + 20 + (height - 20 - trackHeight) / 2;
        int thumbWidth = 18;
        int thumbHeight = 18;

        int trackBorderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;
        int trackBgColor = disabled ? 0xFFCFD0D4 : 0xFF8C8D90;
        int trackProcessColor = disabled ? 0xFFCFD0D4 : 0xFF3C8527;

        extractor.fill(x, trackY, x + width, trackY + trackHeight, trackBorderColor);
        extractor.fill(x + 2, trackY + 2, x + width - 2, trackY + trackHeight - 2, trackBgColor);

        int fillWidth = (int) (this.displayedValue * (width - 4));
        if (fillWidth > 0) {
            extractor.fill(x + 2, trackY + 2, x + 2 + fillWidth,
                    trackY + trackHeight - 2, trackProcessColor);
        }

        int thumbX = x + (int) (this.displayedValue * (width - thumbWidth));
        int thumbY = y + 20 + (height - 20 - thumbHeight) / 2;

        int thumbBorderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;
        int thumbBgColor = disabled ? 0xFFCFD0D4 : (hovered ? 0xFFB1B2B5 : 0xFFD0D1D4);

        extractor.fill(thumbX, thumbY, thumbX + thumbWidth, thumbY + thumbHeight, thumbBorderColor);

        extractor.fill(thumbX + 2, thumbY + 2, thumbX + thumbWidth - 2,
                thumbY + thumbHeight - 2, thumbBgColor);

        int shadowColor = disabled ? 0xFFB0B1B5 : 0xFF58585A;
        extractor.fill(thumbX + 2, thumbY + thumbHeight - 4,
                thumbX + thumbWidth - 2, thumbY + thumbHeight - 2, shadowColor);
    }

    /**
     * A builder class for {@link OreUISlider}.
     */
    public static class OreUIBuilder {
        private final Component message;
        private int x = 0;
        private int y = 0;
        private int width = 200;
        private int height = 44;
        private double value = 0.0;
        private Tooltip tooltip;

        /**
         * Constructs a new {@code OreUIBuilder}.
         *
         * @param message The message to display on the slider.
         */
        public OreUIBuilder(Component message) {
            this.message = message;
        }

        /**
         * Sets the position of the slider.
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
         * Sets the size of the slider.
         *
         * @param width  The width.
         * @param height The height.
         * @return This builder instance.
         */
        public OreUIBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the initial value of the slider.
         *
         * @param value The initial value (0.0 to 1.0).
         * @return This builder instance.
         */
        public OreUIBuilder value(double value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the tooltip for the slider.
         *
         * @param tooltip The tooltip.
         * @return This builder instance.
         */
        public OreUIBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link OreUISlider}.
         *
         * @return A new {@link OreUISlider} instance.
         */
        public OreUISlider build() {
            OreUISlider slider = new OreUISlider(x, y, width, height, message, value);
            if (this.tooltip != null) {
                slider.setTooltip(this.tooltip);
            }
            return slider;
        }
    }
}
