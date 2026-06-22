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

import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

/**
 * A native Minecraft text field (EditBox) component styled to look like an OreUI TextField.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUITextField textField = OreUITextField.builder(font, Component.literal("Enter text..."))
 *     .pos(10, 10).size(200, 40).tooltip(Tooltip.create(Component.literal("Tooltip"))).build();
 * textField.setValue("Initial value");
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUITextField extends EditBox {

    private int fullX;
    private int fullY;
    private int fullWidth;
    private int fullHeight;

    private boolean hasTitle;
    private Component renderedMessageActive;
    private Component renderedMessageInactive;

    /**
     * Constructs a new {@code OreUITextField}.
     *
     * @param font   The font to use.
     * @param x      The x-position.
     * @param y      The y-position.
     * @param width  The width.
     * @param height The height.
     * @param title  The title component.
     */
    public OreUITextField(Font font, int x, int y, int width, int height, Component title) {
        super(font, x, y, width, height, title);
        this.fullX = x;
        this.fullY = y;
        this.fullWidth = width;
        this.fullHeight = height;
        setBordered(false);
        updateCachedMessage(title);
        updateBoxBounds();
    }

    /**
     * Creates a new builder for an {@code OreUITextField}.
     *
     * @param font  The font to use.
     * @param title The title component.
     * @return A new {@link OreUIBuilder} instance.
     */
    public static OreUIBuilder builder(Font font, Component title) {
        return new OreUIBuilder(font, title);
    }

    @Override
    public void setMessage(Component message) {
        super.setMessage(message);
        updateCachedMessage(message);
        updateBoxBounds();
    }

    private void updateCachedMessage(Component message) {
        this.hasTitle = message != null && !message.getString().isEmpty();
        if (this.hasTitle) {
            this.renderedMessageActive = message.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
            this.renderedMessageInactive = message.copy().withStyle(Style.EMPTY.withColor(0xFF48494A));
        } else {
            this.renderedMessageActive = null;
            this.renderedMessageInactive = null;
        }
    }

    @Override
    public void setX(int x) {
        this.fullX = x;
        updateBoxBounds();
    }

    @Override
    public void setY(int y) {
        this.fullY = y;
        updateBoxBounds();
    }

    @Override
    public void setWidth(int width) {
        this.fullWidth = width;
        updateBoxBounds();
    }

    /**
     * Updates the bounds of the underlying EditBox based on the full widget bounds and title presence.
     */
    private void updateBoxBounds() {
        int shiftY = this.hasTitle ? 20 : 0;

        super.setX(this.fullX + 4);
        super.setY(this.fullY + shiftY + (this.fullHeight - shiftY - 8) / 2);
        super.setWidth(this.fullWidth - 8);
        super.setHeight(this.fullHeight - shiftY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible &&
                mouseX >= this.fullX && mouseX < (this.fullX + this.fullWidth) &&
                mouseY >= this.fullY && mouseY < (this.fullY + this.fullHeight);
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return false;
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                         int mouseX, int mouseY, float partialTick) {
        int x = this.fullX;
        int y = this.fullY;
        int width = this.fullWidth;
        int height = this.fullHeight;

        int bgColor = active ? 0xFF313233 : 0xFFD0D1D4;
        int borderColor = active ? 0xFF1E1E1F : 0xFF8C8D90;
        int innerShadowColor = active ? 0xFF242425 : 0xFFB1B2B5;

        if (this.hasTitle) {
            Component renderedMessage = active ? this.renderedMessageActive : this.renderedMessageInactive;

            extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                    .accept(TextAlignment.LEFT, x, y, renderedMessage);
        }

        int boxX = this.fullX;
        int boxY = this.fullY + (this.hasTitle ? 20 : 0);
        int boxWidth = this.fullWidth;
        int boxHeight = this.fullHeight - (this.hasTitle ? 20 : 0);

        extractor.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, borderColor);
        extractor.fill(boxX + 2, boxY + 2, boxX + boxWidth - 2, boxY + boxHeight - 2, bgColor);
        if (active) {
            extractor.fill(boxX + 2, boxY + 2, boxX + boxWidth - 2, boxY + 6, innerShadowColor);
        }

        this.setTextColor(active ? 0xFFFFFFFF : 0xFF48494A);
        super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);
    }

    /**
     * A builder class for {@link OreUITextField}.
     */
    public static class OreUIBuilder {
        private final Font font;
        private final Component title;
        private int x = 0;
        private int y = 0;
        private int width = 200;
        private int height = 44;
        private Tooltip tooltip;

        /**
         * Constructs a new {@code OreUIBuilder}.
         *
         * @param font  The font to use.
         * @param title The title component.
         */
        public OreUIBuilder(Font font, Component title) {
            this.font = font;
            this.title = title;
        }

        /**
         * Sets the position of the text field.
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
         * Sets the size of the text field.
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
         * Sets the tooltip for the text field.
         *
         * @param tooltip The tooltip.
         * @return This builder instance.
         */
        public OreUIBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link OreUITextField}.
         *
         * @return A new {@link OreUITextField} instance.
         */
        public OreUITextField build() {
            OreUITextField textField = new OreUITextField(font, x, y, width, height, title);
            if (this.tooltip != null) {
                textField.setTooltip(this.tooltip);
            }
            return textField;
        }
    }
}
