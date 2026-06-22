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
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A native Minecraft dropdown component styled to look like an OreUI dropdown.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUIDropdown dropdown = OreUIDropdown.builder(Component.literal("Options"))
 *     .pos(10, 10).size(150, 24)
 *     .options(List.of(Component.literal("Option A"), Component.literal("Option B")))
 *     .onSelect(index -> System.out.println("Selected index: " + index))
 *     .build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUIDropdown extends AbstractWidget {

    private static final Component ARROW_UP_ACTIVE = Component.literal("▲").withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
    private static final Component ARROW_UP_INACTIVE = Component.literal("▲").withStyle(Style.EMPTY.withColor(0xFF48494A));
    private static final Component ARROW_DOWN_ACTIVE = Component.literal("▼").withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
    private static final Component ARROW_DOWN_INACTIVE = Component.literal("▼").withStyle(Style.EMPTY.withColor(0xFF48494A));
    private final List<Component> options;
    private final List<Component> cachedOptionsActive;
    private final List<Component> cachedOptionsInactive;
    private final Consumer<Integer> onSelect;
    private final int collapsedHeight;
    private Component cachedMessageActive;
    private Component cachedMessageInactive;
    private int selectedIndex = -1;
    private boolean expanded = false;

    private boolean expandUpwards = false;
    private int scrollOffset = 0;
    private int maxVisibleOptions = 0;

    /**
     * Constructs a new {@code OreUIDropdown}.
     *
     * @param x        The x-position.
     * @param y        The y-position.
     * @param width    The dropdown width.
     * @param height   The dropdown height when collapsed.
     * @param message  The default message when no option is selected.
     * @param options  The list of available options.
     * @param onSelect The callback for when an option is selected.
     */
    protected OreUIDropdown(int x, int y, int width, int height,
                            Component message, List<Component> options, Consumer<Integer> onSelect) {
        super(x, y, width, height, message);
        this.collapsedHeight = height;
        this.options = new ArrayList<>(options);
        this.cachedOptionsActive = new java.util.ArrayList<>(options.size());
        for (Component opt : options) {
            this.cachedOptionsActive.add(opt.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF).withBold(false)));
        }
        this.cachedOptionsInactive = new java.util.ArrayList<>(options.size());
        for (Component opt : options) {
            this.cachedOptionsInactive.add(opt.copy().withStyle(Style.EMPTY.withColor(0xFF48494A).withBold(false)));
        }
        this.maxVisibleOptions = options.size();
        this.onSelect = onSelect;
        updateCachedMessages();
    }

    /**
     * Creates a new builder for an {@code OreUIDropdown}.
     *
     * @param message The default message.
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
        this.cachedMessageActive = msg.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF).withBold(false));
        this.cachedMessageInactive = msg.copy().withStyle(Style.EMPTY.withColor(0xFF48494A).withBold(false));
    }

    /**
     * Checks if the dropdown is currently expanded.
     *
     * @return {@code true} if expanded, {@code false} otherwise.
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Sets whether the dropdown is expanded.
     *
     * @param expanded {@code true} to expand, {@code false} to collapse.
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) return true;
        if (expanded) {
            int listY = expandUpwards ? getY() - maxVisibleOptions * collapsedHeight : getY() + collapsedHeight;
            int listHeight = maxVisibleOptions * collapsedHeight;
            return mouseX >= getX() && mouseX < getX() + getWidth() &&
                    mouseY >= listY && mouseY < listY + listHeight;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (expanded && maxVisibleOptions < options.size()) {
            if (scrollY > 0) {
                scrollOffset = Math.max(0, scrollOffset - 1);
            } else if (scrollY < 0) {
                scrollOffset = Math.min(options.size() - maxVisibleOptions, scrollOffset + 1);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    /**
     * Gets the currently selected index.
     *
     * @return The selected index, or -1 if none.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the selected index.
     *
     * @param index The index to select.
     */
    public void setSelectedIndex(int index) {
        if (index >= -1 && index < options.size()) {
            this.selectedIndex = index;
            if (this.onSelect != null && index != -1) {
                this.onSelect.accept(index);
            }
        }
    }

    private void calculateExpansionDirection() {
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int spaceBelow = screenHeight - (getY() + collapsedHeight) - 50;
        int spaceAbove = getY() - 40;
        int requiredHeight = options.size() * collapsedHeight;

        if (spaceBelow >= requiredHeight) {
            expandUpwards = false;
        } else if (spaceAbove >= requiredHeight) {
            expandUpwards = true;
        } else {
            expandUpwards = spaceBelow < spaceAbove;
        }
    }

    private void calculateExpansionBounds() {
        calculateExpansionDirection();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int spaceBelow = screenHeight - (getY() + collapsedHeight) - 50;
        int spaceAbove = getY() - 40;

        if (expandUpwards) {
            maxVisibleOptions = Math.min(options.size(), Math.max(1, spaceAbove / collapsedHeight));
        } else {
            maxVisibleOptions = Math.min(options.size(), Math.max(1, spaceBelow / collapsedHeight));
        }
        scrollOffset = 0;
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean isDouble) {
        if (!this.active) return;

        int mouseX = (int) mouseX;
        int mouseY = (int) mouseY;

        this.playDownSound(Minecraft.getInstance().getSoundManager());

        if (!expanded) {
            calculateExpansionBounds();
            setExpanded(true);
        } else {
            int listY = expandUpwards ? getY() - maxVisibleOptions * collapsedHeight : getY() + collapsedHeight;
            int listHeight = maxVisibleOptions * collapsedHeight;
            if (mouseX >= getX() && mouseX <= getX() + getWidth() &&
                    mouseY >= listY && mouseY < listY + listHeight) {
                int clickedIndex = scrollOffset + (mouseY - listY) / collapsedHeight;
                if (clickedIndex >= 0 && clickedIndex < options.size()) {
                    setSelectedIndex(clickedIndex);
                }
            }
            setExpanded(false);
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics,
                                            int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();

        boolean disabled = !active;
        boolean hoveredCollapsed = (mouseX >= x && mouseX < x + width &&
                mouseY >= y && mouseY < y + collapsedHeight);

        int bgColor = hoveredCollapsed && !disabled ? 0xFFB1B2B5 : 0xFFD0D1D4;
        int borderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;
        int textColor = disabled ? 0xFF48494A : 0xFFFFFFFF;

        guiGraphics.fill(x, y, x + width, y + collapsedHeight, borderColor);
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + collapsedHeight - 2, bgColor);

        Component displayText;
        if (selectedIndex >= 0) {
            displayText = disabled ? cachedOptionsInactive.get(selectedIndex) : cachedOptionsActive.get(selectedIndex);
        } else {
            displayText = disabled ? cachedMessageInactive : cachedMessageActive;
        }

        guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, displayText, x + width / 2, y + (collapsedHeight - 8) / 2, 0xFFFFFFFF);

        if (!expanded) {
            calculateExpansionDirection();
        }

        Component arrow;
        if (expandUpwards) {
            arrow = disabled ? ARROW_UP_INACTIVE : ARROW_UP_ACTIVE;
        } else {
            arrow = disabled ? ARROW_DOWN_INACTIVE : ARROW_DOWN_ACTIVE;
        }

        guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE)
                .accept(TextAlignment.RIGHT, x + width - 8, y + (collapsedHeight - 8) / 2,
                        arrow);
    }

    /**
     * Renders the expanded list of options.
     *
     * @param extractor   The graphics guiGraphics.
     * @param mouseX      The mouse x-position.
     * @param mouseY      The mouse y-position.
     * @param partialTick The partial tick time.
     */
    public void extractExpandedList(@NotNull GuiGraphics guiGraphics,
                                    int mouseX, int mouseY, float partialTick) {
        if (!expanded) return;

        int x = getX();
        int width = getWidth();
        boolean disabled = !active;
        int borderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;
        int textColor = disabled ? 0xFF48494A : 0xFFFFFFFF;

        int listY = expandUpwards ? getY() - maxVisibleOptions * collapsedHeight : getY() + collapsedHeight;
        int listHeight = maxVisibleOptions * collapsedHeight;

        guiGraphics.fill(x + 2, listY + 2, x + width + 2, listY + listHeight + 2, 0x40000000);
        guiGraphics.fill(x, listY, x + width, listY + listHeight, borderColor);
        guiGraphics.fill(x + 1, listY + 1, x + width - 1, listY + listHeight - 1, 0xFFD0D1D4);

        for (int i = 0; i < maxVisibleOptions; i++) {
            int optionIndex = scrollOffset + i;
            if (optionIndex >= options.size()) break;

            int optionY = listY + i * collapsedHeight;
            boolean optionHovered = mouseX >= x && mouseX < x + width &&
                    mouseY >= optionY && mouseY < optionY + collapsedHeight;

            int itemBgColor = 0xFFD0D1D4;
            if (optionHovered) {
                itemBgColor = 0xFFB1B2B5;
            }

            guiGraphics.fill(x + 1, optionY, x + width - 1, optionY + collapsedHeight, itemBgColor);

            if (i > 0) {
                guiGraphics.fill(x + 1, optionY, x + width - 1, optionY + 1, 0xFFB1B2B5);
            }

            guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, disabled ? cachedOptionsInactive.get(optionIndex) : cachedOptionsActive.get(optionIndex), x + width / 2, optionY + (collapsedHeight - 8) / 2, 0xFFFFFFFF);
        }

        if (maxVisibleOptions < options.size()) {
            int scrollbarWidth = 4;
            int scrollbarX = x + width - 6;

            int trackColor = 0xFF1E1E1F;
            int trackInnerColor = 0xFF48494A;

            // Draw track
            guiGraphics.fill(scrollbarX, listY, scrollbarX + scrollbarWidth, listY + listHeight, trackColor);
            guiGraphics.fill(scrollbarX + 1, listY, scrollbarX + scrollbarWidth - 1, listY + listHeight, trackInnerColor);

            // Draw thumb
            int thumbHeight = Math.max(8, (int) ((float) listHeight * maxVisibleOptions / options.size()));
            int thumbY = listY + (int) ((float)
                    (listHeight - thumbHeight) * scrollOffset / (options.size() - maxVisibleOptions));

            boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= listY && mouseY <= listY + listHeight;
            int thumbColor = hovered ? 0xFFFFFFFF : 0xFFD0D1D4;

            guiGraphics.fill(scrollbarX + 1, thumbY + 1, scrollbarX + scrollbarWidth - 1,
                    thumbY + thumbHeight - 1, thumbColor);
        }
    }

    /**
     * A builder class for creating {@link OreUIDropdown} instances.
     */
    public static class OreUIBuilder {
        private final Component message;
        private int x = 0;
        private int y = 0;
        private int width = 150;
        private int height = 24;
        private List<Component> options = new ArrayList<>();
        private Consumer<Integer> onSelect;
        private Tooltip tooltip;

        /**
         * Constructs a new {@code OreUIBuilder}.
         *
         * @param message The default message.
         */
        public OreUIBuilder(Component message) {
            this.message = message;
        }

        /**
         * Sets the position of the dropdown.
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
         * Sets the size of the dropdown.
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
         * Sets the options for the dropdown.
         *
         * @param options The list of options.
         * @return This builder instance.
         */
        public OreUIBuilder options(List<Component> options) {
            this.options = new ArrayList<>(options);
            return this;
        }

        /**
         * Sets the callback for when an option is selected.
         *
         * @param onSelect The callback.
         * @return This builder instance.
         */
        public OreUIBuilder onSelect(Consumer<Integer> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        /**
         * Sets the tooltip for the dropdown.
         *
         * @param tooltip The {@link Tooltip}.
         * @return This builder instance.
         */
        public OreUIBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link OreUIDropdown} instance.
         *
         * @return A new {@link OreUIDropdown}.
         */
        public OreUIDropdown build() {
            OreUIDropdown dropdown = new OreUIDropdown(x, y, width, height, message, options, onSelect);
            if (this.tooltip != null) {
                dropdown.setTooltip(this.tooltip);
            }
            return dropdown;
        }
    }
}
