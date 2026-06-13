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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

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
        super(minecraft, width, height, y, itemHeight);
        this.itemHeight = itemHeight;
        this.setX(x);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        double mouseX = event.x();
        double mouseY = event.y();

        if (this.scrollable() && mouseX >= this.scrollBarX() && mouseX <= this.scrollBarX() + 4 &&
                mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()) {
            return super.mouseClicked(event, isDouble);
        }

        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                if (entry.mouseClicked(event, isDouble)) {
                    this.setSelected(entry);
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.getChildWidget() instanceof OreUIDropdown dropdown) {
                if (dropdown.isExpanded() && dropdown.isMouseOver(mouseX, mouseY)) {
                    if (dropdown.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                        return true;
                    }
                }
            }
        }

        double prevScroll = this.scrollAmount();
        boolean handled = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        // Workaround for SmoothScrolling mod which reverts scrollAmount
        // Since we override extractScrollbar without calling super, their animation never happens,
        // resulting in the scrollbar getting stuck. We force the scroll update here.
        if (handled && prevScroll == this.scrollAmount() && scrollY != 0) {
            this.setScrollAmount(this.scrollAmount() - scrollY * ((double) this.itemHeight / 2.0));
        }

        return handled;
    }

    /**
     * Adds an entry to the scroll list.
     *
     * @param entry The entry to add.
     */
    public void addScrollEntry(OreUIScrollListEntry entry) {
        this.addEntry(entry, Math.max(this.itemHeight, entry.getChildWidget().getHeight()));
    }

    /**
     * Clears all entries from the scroll list.
     */
    public void clearScrollEntries() {
        this.clearEntries();
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                         int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(extractor, mouseX, mouseY, partialTick);

        extractor.nextStratum();
        for (OreUIScrollListEntry entry : this.children()) {
            if (entry.getChildWidget() instanceof OreUIDropdown dropdown) {
                if (dropdown.isExpanded()) {
                    dropdown.extractExpandedList(extractor, mouseX, mouseY, partialTick);
                }
            }
        }
    }

    @Override
    protected void extractListBackground(@NonNull GuiGraphicsExtractor extractor) {
        int bgColor = 0x80000000;
        extractor.fill(this.getX(), this.getY(), this.getX() + this.getWidth(),
                this.getY() + this.getHeight(), bgColor);
    }

    @Override
    public int getRowWidth() {
        return this.getWidth() - 16;
    }

    @Override
    protected int scrollBarX() {
        return this.getX() + this.getWidth() - 6;
    }

    @Override
    protected void extractListSeparators(@NonNull GuiGraphicsExtractor extractor) {
        // No explicit separators
    }

    @Override
    protected void extractScrollbar(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        if (!this.scrollable()) {
            return;
        }

        int scrollbarX = this.scrollBarX();
        int scrollbarWidth = 4;
        int scrollerHeight = this.scrollerHeight();
        int scrollerY = this.scrollBarY();

        boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= this.getY() && mouseY <= this.getBottom();

        int trackColor = 0xFF1E1E1F;
        extractor.fill(scrollbarX, this.getY(), scrollbarX + scrollbarWidth, this.getBottom(), trackColor);
        extractor.fill(scrollbarX + 1, this.getY(), scrollbarX + scrollbarWidth - 1, this.getBottom(), 0xFF48494A);

        int thumbColor = hovered ? 0xFFFFFFFF : 0xFFD0D1D4;

        extractor.fill(scrollbarX + 1, scrollerY + 1, scrollbarX + scrollbarWidth - 1,
                scrollerY + scrollerHeight - 1, thumbColor);
    }

    @Override
    protected void extractSelection(@NonNull GuiGraphicsExtractor extractor,
                                    @NonNull OreUIScrollListEntry entry, int i) {
        // Do not render focus box
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
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
        public void extractContent(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY,
                                   boolean hovered, float partialTick) {
            int width = this.getContentWidth();
            int x = this.getContentX();
            int y = this.getContentY();

            this.childWidget.setX(x);
            this.childWidget.setY(y);
            this.childWidget.setWidth(width);

            this.childWidget.extractRenderState(extractor, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDouble) {
            return this.childWidget.mouseClicked(event, isDouble);
        }

        @Override
        public boolean mouseReleased(@NonNull MouseButtonEvent event) {
            return this.childWidget.mouseReleased(event);
        }

        @Override
        public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
            return this.childWidget.mouseDragged(event, dragX, dragY);
        }

        @Override
        public boolean keyPressed(@NonNull KeyEvent event) {
            return this.childWidget.keyPressed(event);
        }

        @Override
        public boolean keyReleased(@NonNull KeyEvent event) {
            return this.childWidget.keyReleased(event);
        }

        @Override
        public boolean charTyped(@NonNull CharacterEvent event) {
            return this.childWidget.charTyped(event);
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
