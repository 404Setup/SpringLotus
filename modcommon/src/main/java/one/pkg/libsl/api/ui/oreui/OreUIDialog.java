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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A native Minecraft screen styled to look like an OreUI Modal Dialog.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUIDialog dialog = new OreUIDialog(Component.literal("My Dialog"), lastScreen)
 *      .content(Component.literal("This is some custom content text!"))
 *      .onConfirm(() -> System.out.println("Confirmed!"));
 * Minecraft.getInstance().setScreen(dialog);
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUIDialog extends Screen {

    private final Screen lastScreen;
    private final Component styledTitle;
    private final List<AbstractWidget> customWidgets = new ArrayList<>();
    private int modalWidth = 300;
    private int modalHeight = 200;
    private Component content;
    private Runnable onConfirm;
    private Runnable onCancel;
    private boolean showCancel = true;
    private Component confirmText = CommonComponents.GUI_DONE;
    private Component cancelText = CommonComponents.GUI_CANCEL;
    private net.minecraft.resources.ResourceLocation image;
    private int imageWidth;
    private int imageHeight;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private long openTime;
    private boolean isClosing = false;
    private long closeTime;

    /**
     * Constructs a new {@code OreUIDialog}.
     *
     * @param title      The dialog title.
     * @param lastScreen The screen to return to after closing.
     */
    public OreUIDialog(Component title, Screen lastScreen) {
        super(title);
        this.lastScreen = (lastScreen instanceof OreUIDialog) ? ((OreUIDialog) lastScreen).lastScreen : lastScreen;
        this.styledTitle = title.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
    }

    /**
     * Sets the dimensions of the dialog.
     *
     * @param width  The modal width.
     * @param height The modal height.
     * @return This dialog instance.
     */
    public OreUIDialog setDimensions(int width, int height) {
        this.modalWidth = width;
        this.modalHeight = height;
        return this;
    }

    /**
     * Sets the content component of the dialog.
     *
     * @param content The content text.
     * @return This dialog instance.
     */
    public OreUIDialog content(Component content) {
        this.content = content;
        return this;
    }

    /**
     * Shows an image in the dialog.
     *
     * @param image  The image identifier.
     * @param width  The image width.
     * @param height The image height.
     * @return This dialog instance.
     */
    public OreUIDialog show(ResourceLocation image, int width, int height) {
        this.image = image;
        this.imageWidth = width;
        this.imageHeight = height;
        return this;
    }

    /**
     * Sets the action to perform when the confirm button is clicked.
     *
     * @param onConfirm The confirm action.
     * @return This dialog instance.
     */
    public OreUIDialog onConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    /**
     * Sets the action to perform when the cancel button is clicked.
     *
     * @param onCancel The cancel action.
     * @return This dialog instance.
     */
    public OreUIDialog onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * Sets whether to show the cancel button.
     *
     * @param showCancel {@code true} to show, {@code false} otherwise.
     * @return This dialog instance.
     */
    public OreUIDialog showCancel(boolean showCancel) {
        this.showCancel = showCancel;
        return this;
    }

    /**
     * Sets the text for the confirm button.
     *
     * @param confirmText The confirm text.
     * @return This dialog instance.
     */
    public OreUIDialog confirmText(Component confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    /**
     * Sets the text for the cancel button.
     *
     * @param cancelText The cancel text.
     * @return This dialog instance.
     */
    public OreUIDialog cancelText(Component cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    /**
     * Adds a custom widget to the dialog.
     *
     * @param widget The widget to add.
     * @param <T>    The widget type.
     * @return The added widget.
     */
    public <T extends AbstractWidget> T addCustomWidget(T widget) {
        this.customWidgets.add(widget);
        return widget;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();
        this.openTime = System.currentTimeMillis();

        int maxAllowedHeight = this.height - 40;

        int contentHeight = 0;
        int calculatedWidth = 200;

        if (this.content != null) {
            int maxTextW = Math.max(200, this.width / 2);
            List<net.minecraft.util.FormattedCharSequence> lines = this.minecraft.font.split(this.content, maxTextW);
            contentHeight = lines.size() * 12 + 10;
            for (var line : lines) {
                calculatedWidth = Math.max(calculatedWidth, this.minecraft.font.width(line) + 40);
            }
        }

        if (this.image != null) {
            calculatedWidth = Math.max(calculatedWidth, this.imageWidth + 40);
            contentHeight += this.imageHeight + 10;
        }

        int customWidgetsHeight = 0;
        for (AbstractWidget w : customWidgets) {
            calculatedWidth = Math.max(calculatedWidth, w.getWidth() + 40);
            customWidgetsHeight = Math.max(customWidgetsHeight, w.getY() + w.getHeight());
        }

        if (this.modalWidth < calculatedWidth) {
            this.modalWidth = calculatedWidth;
        }
        int neededHeight = 40 + contentHeight + customWidgetsHeight + 50;
        if (this.modalHeight < neededHeight || (this.modalHeight == 200 && neededHeight < 200)) {
            this.modalHeight = neededHeight;
        }

        if (this.modalHeight > maxAllowedHeight) {
            this.maxScroll = neededHeight - maxAllowedHeight;
            this.modalHeight = maxAllowedHeight;
        } else {
            this.maxScroll = 0;
        }

        int startX = (this.width - this.modalWidth) / 2;
        int startY = (this.height - this.modalHeight) / 2;

        int btnWidth = showCancel ? Math.min(100, (this.modalWidth - 40) / 2) : Math.min(200, this.modalWidth - 40);
        int btnY = startY + this.modalHeight - 34;

        if (showCancel) {
            int totalWidth = btnWidth * 2 + 10;
            int btnStartX = startX + (this.modalWidth - totalWidth) / 2;
            super.addRenderableWidget(OreUIButton.oreUIBuilder(cancelText, b -> {
                if (onCancel != null) onCancel.run();
                this.close();
            }).pos(btnStartX, btnY).size(btnWidth, 24).style(OreUIButton.Style.FLAT).build());

            super.addRenderableWidget(OreUIButton.oreUIBuilder(confirmText, b -> {
                if (onConfirm != null) onConfirm.run();
                this.close();
            }).pos(btnStartX + btnWidth + 10, btnY).size(btnWidth, 24).style(OreUIButton.Style.GREEN).build());
        } else {
            int btnStartX = startX + (this.modalWidth - btnWidth) / 2;
            super.addRenderableWidget(OreUIButton.oreUIBuilder(confirmText, b -> {
                if (onConfirm != null) onConfirm.run();
                this.close();
            }).pos(btnStartX, btnY).size(btnWidth, 24).style(OreUIButton.Style.GREEN).build());
        }

        for (AbstractWidget w : customWidgets) {
            super.addRenderableWidget(w);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (this.maxScroll > 0) {
            this.scrollOffset -= (int) (scrollY * 20);
            this.scrollOffset = net.minecraft.util.Mth.clamp(this.scrollOffset, 0, this.maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private void close() {
        if (!isClosing) {
            isClosing = true;
            closeTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    public void renderBackground(@NotNull GuiGraphics guiGraphics,
                                  int mouseX, int mouseY, float partialTick) {
        // Do nothing here to prevent double rendering.
    }

    private void drawModalBackground(@NotNull GuiGraphics guiGraphics, float partialTick) {
        float animProgress = isClosing ?
                1.0f - Math.min(1.0f, (System.currentTimeMillis() - closeTime) / 150.0f) :
                Math.min(1.0f, (System.currentTimeMillis() - openTime) / 150.0f);

        if (isClosing && animProgress <= 0) {
            this.minecraft.setScreen(this.lastScreen);
            return;
        }

        int alpha = (int) (0xAA * animProgress);
        guiGraphics.fill(0, 0, this.width, this.height, (alpha << 24));

        int startX = (this.width - this.modalWidth) / 2;
        int startY = (this.height - this.modalHeight) / 2;


        int bgColor = 0xFF313233;
        guiGraphics.fill(startX, startY, startX + this.modalWidth, startY + this.modalHeight, bgColor);

        int titleHeight = 40;
        int titleBgColor = 0xFF48494A;
        guiGraphics.fill(startX, startY, startX + this.modalWidth, startY + titleHeight, titleBgColor);

        guiGraphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, this.styledTitle, startX + this.modalWidth / 2, startY + (titleHeight - 8) / 2, 0xFFFFFFFF);


        guiGraphics.fill(startX, startY, startX + this.modalWidth,
                startY + 2, 0x33FFFFFF);
        guiGraphics.fill(startX, startY, startX + 2,
                startY + titleHeight, 0x33FFFFFF);
        guiGraphics.fill(startX + this.modalWidth - 2,
                startY, startX + this.modalWidth, startY + titleHeight, 0x1AFFFFFF);
        guiGraphics.fill(startX, startY + titleHeight - 2,
                startX + this.modalWidth, startY + titleHeight, 0x1AFFFFFF);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics,
                                   int mouseX, int mouseY, float partialTick) {
        if (this.lastScreen != null) {
            this.lastScreen.renderBackground(guiGraphics);
            this.lastScreen.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        drawModalBackground(guiGraphics, partialTick);

        float animProgress = isClosing ?
                1.0f - Math.min(1.0f, (System.currentTimeMillis() - closeTime) / 150.0f) :
                Math.min(1.0f, (System.currentTimeMillis() - openTime) / 150.0f);

        if (animProgress >= 0.1f) {
            int startX = (this.width - this.modalWidth) / 2;
            int startY = (this.height - this.modalHeight) / 2;
            int textY = startY + 40 + 15 - this.scrollOffset;

            guiGraphics.enableScissor(startX, startY + 40, startX + this.modalWidth, startY + this.modalHeight - 40);

            if (this.image != null) {
                int imgX = startX + (this.modalWidth - this.imageWidth) / 2;
                guiGraphics.blit(this.image, imgX, textY, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
                textY += this.imageHeight + 10;
            }

            if (this.content != null) {
                List<net.minecraft.util.FormattedCharSequence> lines = this.minecraft.font.split(this.content, this.modalWidth - 40);
                for (var line : lines) {
                    guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, line, startX + 20, textY, 0xFFFFFFFF, false);
                    textY += 12;
                }
            }

            // Note: custom widgets are drawn by super.extractRenderState which might ignore our scroll offset 
            // since they were added to the screen with absolute positions. 
            // If they are to be scrolled, we would need to manually draw them or adjust their Y.
            // For now, only text and image scroll.

            guiGraphics.disableScissor();

            super.render(guiGraphics, mouseX, mouseY, partialTick);

            if (this.maxScroll > 0) {
                int scrollbarWidth = 4;
                int scrollbarX = startX + this.modalWidth - 6;

                int trackColor = 0xFF1E1E1F;
                int trackInnerColor = 0xFF48494A;

                int listHeight = this.modalHeight - 80;
                int scrollYStart = startY + 40;
                int scrollYEnd = startY + this.modalHeight - 40;

                // Draw track
                guiGraphics.fill(scrollbarX, scrollYStart, scrollbarX + scrollbarWidth, scrollYEnd, trackColor);
                guiGraphics.fill(scrollbarX + 1, scrollYStart, scrollbarX + scrollbarWidth - 1, scrollYEnd, trackInnerColor);

                // Draw thumb
                int totalHeight = listHeight + this.maxScroll;
                int thumbHeight = Math.max(8, (int) ((float) listHeight * listHeight / totalHeight));
                int thumbY = scrollYStart + (int) ((float)
                        (listHeight - thumbHeight) * this.scrollOffset / this.maxScroll);

                boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                        mouseY >= scrollYStart && mouseY <= scrollYEnd;
                int thumbColor = hovered ? 0xFFFFFFFF : 0xFFD0D1D4;

                guiGraphics.fill(scrollbarX + 1, thumbY + 1, scrollbarX + scrollbarWidth - 1,
                        thumbY + thumbHeight - 1, thumbColor);
            }
        }
    }
}
