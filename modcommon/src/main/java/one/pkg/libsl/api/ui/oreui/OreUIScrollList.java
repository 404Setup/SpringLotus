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
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.NotNull;

/**
 * A native Minecraft scrolling list component styled to look like an OreUI scroll view.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUIScrollList list = OreUIScrollList.builder()
 *     .pos(10, 10).size(200, 150)
 *     .itemHeight(30)
 *     .build();
 * list.addEntry(new OreUIScrollList.OreUIScrollListEntry(
 *     OreUIButton.oreUIBuilder(Component.literal("Button"), b -> {}).size(180, 24).build()
 * ));
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUIScrollList extends AbstractSelectionList<OreUIScrollList.OreUIScrollListEntry> {
    @Override
    public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {}

    private final int itemHeight;

    /**
     * Constructs a new {@code OreUIScrollList}.
     *
     * @param minecraft The Minecraft instance.
     * @param x         The x-position.
     * @param y         The y-position.
     * @param width     The list width.
     * @param height    The list height.
     * @param itemHeight The height of each item in the list.
     */
    protected OreUIScrollList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight) {
        super(minecraft, width, height, y, height, itemHeight);
        this.itemHeight = itemHeight;
        
    }

    /**
     * Creates a new builder for an {@code OreUIScrollList}.
     *
     * @return A new {@link OreUIBuilder} instance.
     */
    public static OreUIBuilder builder() {
        return new OreUIBuilder();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if ((this.getMaxScroll() > 0) && mouseX >= this.getScrollbarPosition() && mouseX <= this.getScrollbarPosition() + 4 &&
                mouseY >= this.y0 && mouseY <= this.y0 + this.height) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setSelected(entry);
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.getChildWidget() instanceof OreUIDropdown dropdown) {
                if (dropdown.isExpanded() && dropdown.isMouseOver(mouseX, mouseY)) {
                    if (dropdown.mouseScrolled(mouseX, mouseY, scrollY)) {
                        return true;
                    }
                }
            }
        }

        double prevScroll = this.getScrollAmount();
        boolean handled = super.mouseScrolled(mouseX, mouseY, scrollY);

        // Workaround for SmoothScrolling mod which reverts scrollAmount
        // Since we override extractScrollbar without calling super, their animation never happens,
        // resulting in the scrollbar getting stuck. We force the scroll update here.
        if (handled && prevScroll == this.getScrollAmount() && scrollY != 0) {
            this.setScrollAmount(this.getScrollAmount() - scrollY * ((double) this.itemHeight / 2.0));
        }

        return handled;
    }

    /**
     * Adds an entry to the scroll list.
     *
     * @param entry The entry to add.
     */
    public void addScrollEntry(OreUIScrollListEntry entry) {
        this.addEntry(entry);
    }

    /**
     * Clears all entries from the scroll list.
     */
    public void clearScrollEntries() {
        this.clearEntries();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics,
                                         int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        
        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.getChildWidget() instanceof OreUIDropdown dropdown) {
                if (dropdown.isExpanded()) {
                    dropdown.extractExpandedList(guiGraphics, mouseX, mouseY, partialTick);
                }
            }
        }
    }

    protected void extractListBackground(@NotNull GuiGraphics guiGraphics) {
        int bgColor = 0x80000000;
        guiGraphics.fill(this.x0, this.y0, this.x0 + this.width,
                this.y0 + this.height, bgColor);
    }

    @Override
    public int getRowWidth() {
        return this.width - 16;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x0 + this.width - 6;
    }

    protected void extractListSeparators(@NotNull GuiGraphics guiGraphics) {
        // No explicit separators
    }

    protected void extractScrollbar(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!(this.getMaxScroll() > 0)) {
            return;
        }

        int scrollbarX = this.getScrollbarPosition();
        int scrollbarWidth = 4;
        int scrollerHeight = 0 /* scrollerHeight */;
        int scrollerY = 0 /* scrollBarY */;

        boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= this.y0 && mouseY <= this.y1;

        int trackColor = 0xFF1E1E1F;
        guiGraphics.fill(scrollbarX, this.y0, scrollbarX + scrollbarWidth, this.y1, trackColor);
        guiGraphics.fill(scrollbarX + 1, this.y0, scrollbarX + scrollbarWidth - 1, this.y1, 0xFF48494A);

        int thumbColor = hovered ? 0xFFFFFFFF : 0xFFD0D1D4;

        guiGraphics.fill(scrollbarX + 1, scrollerY + 1, scrollbarX + scrollbarWidth - 1,
                scrollerY + scrollerHeight - 1, thumbColor);
    }

    protected void extractSelection(@NotNull GuiGraphics guiGraphics,
                                    @NotNull OreUIScrollListEntry entry, int i) {
        // Do not render focus box
    }

    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    /**
     * A single entry in the {@link OreUIScrollList}.
     */
    public static class OreUIScrollListEntry extends AbstractSelectionList.Entry<OreUIScrollListEntry> {
        private final AbstractWidget childWidget;

        /**
         * Constructs a new {@code OreUIScrollListEntry}.
         *
         * @param childWidget The widget to be contained in this entry.
         */
        public OreUIScrollListEntry(AbstractWidget childWidget) {
            this.childWidget = childWidget;
        }

        /**
         * Gets the child widget of this entry.
         *
         * @return The child widget.
         */
        public AbstractWidget getChildWidget() {
            return this.childWidget;
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                                   int mouseX, int mouseY, boolean hovered, float partialTick) {
            this.childWidget.setX(left);
            this.childWidget.setY(top);
            this.childWidget.setWidth(width);

            this.childWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.childWidget.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.childWidget.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return this.childWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return this.childWidget.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return this.childWidget.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            return this.childWidget.charTyped(codePoint, modifiers);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            this.childWidget.setFocused(focused);
        }
    }

    /**
     * A builder class for {@link OreUIScrollList}.
     */
    public static class OreUIBuilder {
        private int x = 0;
        private int y = 0;
        private int width = 200;
        private int height = 150;
        private int itemHeight = 30;

        /**
         * Sets the position of the scroll list.
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
         * Sets the size of the scroll list.
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
         * Sets the item height for the scroll list.
         *
         * @param height The height of each item.
         * @return This builder instance.
         */
        public OreUIBuilder itemHeight(int height) {
            this.itemHeight = height;
            return this;
        }

        /**
         * Builds the {@link OreUIScrollList}.
         *
         * @return A new {@link OreUIScrollList} instance.
         */
        public OreUIScrollList build() {
            return new OreUIScrollList(Minecraft.getInstance(), x, y, width, height, itemHeight);
        }
    }
}
