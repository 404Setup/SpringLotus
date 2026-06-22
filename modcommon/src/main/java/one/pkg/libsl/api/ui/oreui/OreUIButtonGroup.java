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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A widget that displays a group of buttons where only one can be selected at a time.
 */
public class OreUIButtonGroup extends AbstractWidget {
    private final List<Component> options;
    private final List<Component> cachedOptions;
    private final Consumer<Integer> onSelect;
    private int selectedIndex;

    /**
     * Constructs a new {@code OreUIButtonGroup}.
     *
     * @param x             The x-position.
     * @param y             The y-position.
     * @param width         The total width of the group.
     * @param height        The height of the buttons.
     * @param options       The list of components representing each button's text.
     * @param selectedIndex The initially selected index.
     * @param onSelect      The callback when a button is selected.
     */
    protected OreUIButtonGroup(int x, int y, int width, int height, List<Component> options,
                               int selectedIndex, Consumer<Integer> onSelect) {
        super(x, y, width, height, Component.empty());
        this.options = options;
        this.cachedOptions = new ArrayList<>(options.size());
        for (Component opt : options) {
            this.cachedOptions.add(opt.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF).withBold(false)));
        }
        this.selectedIndex = selectedIndex;
        this.onSelect = onSelect;
    }

    /**
     * Creates a new builder for an {@code OreUIButtonGroup}.
     *
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY,
                                         float partialTick) {
        int btnWidth = width / Math.max(1, options.size());
        for (int i = 0; i < options.size(); i++) {
            int btnX = getX() + i * btnWidth;
            boolean isSelected = i == selectedIndex;
            boolean isHovered = mouseX >= btnX &&
                    mouseX < btnX + btnWidth && mouseY >= getY() && mouseY < getY() + height;

            int bgColor = isSelected ? 0xFF3C8527 : (isHovered ? 0xFFB1B2B5 : 0xFFD0D1D4);
            int borderColor = 0xFF1E1E1F;

            guiGraphics.fill(btnX, getY(), btnX + btnWidth, getY() + height, borderColor);
            guiGraphics.fill(btnX + 1, getY() + 1, btnX + btnWidth - 1, getY() + height - 1, bgColor);

            guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, cachedOptions.get(i), btnX + btnWidth / 2, getY() + (height - 8) / 2, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active) return false;
        int btnWidth = width / Math.max(1, options.size());
        int clickedIndex = ((int) mouseX - getX()) / btnWidth;
        if (clickedIndex >= 0 && clickedIndex < options.size() && clickedIndex != selectedIndex) {
            selectedIndex = clickedIndex;
            if (onSelect != null) onSelect.accept(selectedIndex);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    /**
     * A builder class for creating {@link OreUIButtonGroup} instances.
     */
    public static class Builder {
        private int x, y, width = 150, height = 24;
        private List<Component> options;
        private int selectedIndex = 0;
        private Consumer<Integer> onSelect;

        /**
         * Sets the position of the button group.
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
         * Sets the size of the button group.
         *
         * @param width  The total width.
         * @param height The height.
         * @return This builder instance.
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Sets the options for the button group.
         *
         * @param options The list of button labels.
         * @return This builder instance.
         */
        public Builder options(List<Component> options) {
            this.options = options;
            return this;
        }

        /**
         * Sets the initially selected index.
         *
         * @param index The selected index.
         * @return This builder instance.
         */
        public Builder selectedIndex(int index) {
            this.selectedIndex = index;
            return this;
        }

        /**
         * Sets the callback for when a selection is made.
         *
         * @param onSelect The callback.
         * @return This builder instance.
         */
        public Builder onSelect(Consumer<Integer> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        /**
         * Builds the {@link OreUIButtonGroup} instance.
         *
         * @return A new {@link OreUIButtonGroup}.
         */
        public OreUIButtonGroup build() {
            return new OreUIButtonGroup(x, y, width, height, options, selectedIndex, onSelect);
        }
    }
}
