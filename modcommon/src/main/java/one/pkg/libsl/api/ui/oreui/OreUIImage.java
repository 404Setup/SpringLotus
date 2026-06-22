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

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * A native Minecraft component styled to display an image for OreUI.
 */
@SuppressWarnings("unused")
public class OreUIImage extends AbstractWidget {

    private final Identifier image;
    private final int textureWidth;
    private final int textureHeight;

    /**
     * Constructs a new {@code OreUIImage}.
     *
     * @param x             The x-position.
     * @param y             The y-position.
     * @param width         The image width.
     * @param height        The image height.
     * @param image         The image identifier.
     * @param textureWidth  The full texture width.
     * @param textureHeight The full texture height.
     */
    public OreUIImage(int x, int y, int width, int height, Identifier image, int textureWidth, int textureHeight) {
        super(x, y, width, height, Component.empty());
        this.image = image;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /**
     * Constructs a new {@code OreUIImage} with texture size matching widget size.
     *
     * @param x      The x-position.
     * @param y      The y-position.
     * @param width  The image width.
     * @param height The image height.
     * @param image  The image identifier.
     */
    public OreUIImage(int x, int y, int width, int height, Identifier image) {
        this(x, y, width, height, image, width, height);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        extractor.blit(this.image, getX(), getY(), 0, 0, getWidth(), getHeight(), this.textureWidth, this.textureHeight);
    }
}
