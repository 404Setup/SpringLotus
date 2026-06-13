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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

/**
 * An example screen demonstrating various OreUI-styled components.
 */
public class OreUIExampleScreen extends Screen {

    private final Screen lastScreen;
    private final Component cachedTitle;
    private OreUIScrollList scrollList;

    /**
     * Constructs a new {@code OreUIExampleScreen}.
     *
     * @param lastScreen The screen to return to after closing.
     */
    public OreUIExampleScreen(Screen lastScreen) {
        super(Component.literal("OreUI Examples"));
        this.lastScreen = lastScreen;
        this.cachedTitle = this.title.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF).withBold(true));
    }

    @Override
    protected void init() {
        int listWidth = Math.min(this.width - 40, 400);
        int listX = (this.width - listWidth) / 2;
        int listY = 40;
        int listHeight = this.height - listY - 50;

        if (listHeight <= 0) return;

        scrollList = OreUIScrollList.builder()
                .pos(listX, listY)
                .size(listWidth, listHeight)
                .itemHeight(44)
                .build();
        this.addRenderableWidget(scrollList);

        // Button
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUIButton.oreUIBuilder(Component.literal("Normal Button"), b -> {
                        })
                        .size(listWidth - 20, 24).build()
        ));

        // Green Button
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUIButton.oreUIBuilder(Component.literal("Green Button"), b -> {
                        })
                        .size(listWidth - 20, 24).style(OreUIButton.Style.GREEN).build()
        ));

        // Switch
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUISwitch.builder(Component.literal("Switch Component"), s -> {
                        })
                        .pos(0, 0).build()
        ));

        // Slider
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUISlider.builder(Component.literal("Slider Component"))
                        .pos(0, 0).size(listWidth - 20, 24).value(0.5).build()
        ));

        // Text Field
        OreUITextField textField = OreUITextField.builder(this.minecraft.font, Component.literal("Text Field"))
                .pos(0, 0).size(listWidth - 20, 44).build();
        textField.setMaxLength(128);
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(textField));

        // Tag
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUITag.builder(Component.literal("Tag Component"))
                        .pos(0, 0).size(listWidth - 20, 24).style(OreUITag.Style.BLACK).build()
        ));

        // Button Group
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUIButtonGroup.builder()
                        .pos(0, 0).size(listWidth - 20, 24)
                        .options(Arrays.asList(Component.literal("Option 1"), Component.literal("Option 2")))
                        .selectedIndex(0).onSelect(idx -> {
                        }).build()
        ));

        // Dropdown
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUIDropdown.builder(Component.literal("Dropdown Component"))
                        .pos(0, 0).size(listWidth - 20, 24)
                        .options(Arrays.asList(Component.literal("Dropdown 1"), Component.literal("Dropdown 2"), Component.literal("Dropdown 3")))
                        .onSelect(idx -> {
                        }).build()
        ));

        // Dialog Trigger
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
                OreUIButton.oreUIBuilder(Component.literal("Open Dialog"), b -> {
                    OreUIDialog dialog = new OreUIDialog(Component.literal("Example Dialog"), this)
                            .content(Component.literal("This is an example dialog displaying the newly added features like auto-resizing, content text, and confirm/cancel buttons."))
                            .onConfirm(() -> System.out.println("Dialog Confirmed!"))
                            .onCancel(() -> System.out.println("Dialog Canceled!"));
                    this.minecraft.setScreen(dialog);
                }).size(listWidth - 20, 24).style(OreUIButton.Style.FLAT).build()
        ));

        // Back Button
        this.addRenderableWidget(OreUIButton.oreUIBuilder(CommonComponents.GUI_BACK, _ -> this.minecraft.setScreen(this.lastScreen)).pos(this.width / 2 - 100, this.height - 40).size(200, 24).style(OreUIButton.Style.GREEN).build());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        extractor.fill(0, 0, this.width, this.height, 0xFF2A2B2D); // background

        extractor.textRenderer().accept(
                TextAlignment.CENTER,
                this.width / 2, 20,
                this.cachedTitle
        );

        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
    }
}
