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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * A native Minecraft tag component styled to look like an OreUI tag.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUITag tag = OreUITag.builder(Component.literal("New")).style(OreUITag.Style.GREEN).pos(10, 10).build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUITag extends AbstractWidget {

    private final Style style;
    private Component cachedMessage;

    /**
     * Constructs a new {@code OreUITag}.
     *
     * @param x       The x-position.
     * @param y       The y-position.
     * @param width   The width.
     * @param height  The height.
     * @param message The message to display.
     * @param style   The style of the tag.
     */
    protected OreUITag(int x, int y, int width, int height, Component message, Style style) {
        super(x, y, width, height, message);
        this.style = style;
        this.active = false; // Tags are not interactive
        updateCachedMessage();
    }

    /**
     * Creates a new builder for an {@code OreUITag}.
     *
     * @param message The message to display.
     * @return A new {@link Builder} instance.
     */
    public static Builder builder(Component message) {
        return new Builder(message);
    }

    @Override
    public void setMessage(@NotNull Component message) {
        super.setMessage(message);
        updateCachedMessage();
    }

    private void updateCachedMessage() {
        this.cachedMessage = getMessage().copy().withStyle(
                net.minecraft.network.chat.Style.EMPTY.withColor(this.style.textColor)
        );
    }

    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                         int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        extractor.fill(x, y, x + width, y + height, style.bgColor);

        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                .accept(TextAlignment.CENTER,
                        x + width / 2,
                        y + (height - 8) / 2,
                        this.cachedMessage
                );
    }

    /**
     * Styles for the {@link OreUITag}.
     */
    public enum Style {
        /**
         * Black background with white text.
         */
        BLACK(0xFF1E1E1F, 0xFFFFFFFF),
        /**
         * Green background with black text.
         */
        GREEN(0xFF6CC349, 0xFF000000),
        /**
         * Blue background with black text.
         */
        BLUE(0xFF8CB3FF, 0xFF000000),
        /**
         * Yellow background with black text.
         */
        YELLOW(0xFFFFE866, 0xFF000000),
        /**
         * Red background with black text.
         */
        RED(0xFFF46D6D, 0xFF000000);

        final int bgColor;
        final int textColor;

        Style(int bgColor, int textColor) {
            this.bgColor = bgColor;
            this.textColor = textColor;
        }
    }

    /**
     * A builder class for {@link OreUITag}.
     */
    public static class Builder {
        private final Component message;
        private int x = 0;
        private int y = 0;
        private int width = 40;
        private int height = 16;
        private Style style = Style.BLACK;

        /**
         * Constructs a new {@code Builder}.
         *
         * @param message The message to display.
         */
        public Builder(Component message) {
            this.message = message;
        }

        /**
         * Sets the position of the tag.
         *
         * @param x The x-position.
         * @param y The y-position.
         * @return This builder instance.
         */
        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the size of the tag.
         *
         * @param width  The width.
         * @param height The height.
         * @return This builder instance.
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the style of the tag.
         *
         * @param style The style.
         * @return This builder instance.
         */
        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        /**
         * Builds the {@link OreUITag}.
         *
         * @return A new {@link OreUITag} instance.
         */
        public OreUITag build() {
            return new OreUITag(x, y, width, height, message, style);
        }
    }
}
