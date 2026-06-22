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

/**
 * Utility class for rendering OreUI components.
 * Provides helper methods for drawing rectangles and shadows to simulate the OreUI styling.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * public void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
 *     OreUIRenderUtils.fillBorder(extractor, getX(), getY(), getWidth(), getHeight(), 0xFF1E1E1F);
 * }
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUIRenderUtils {

    /**
     * Draws a filled rectangle with a 1-pixel inset border.
     *
     * @param extractor   the GuiGraphicsExtractor instance
     * @param x           the top-left x coordinate
     * @param y           the top-left y coordinate
     * @param width       the width of the rectangle
     * @param height      the height of the rectangle
     * @param fillColor   the ARGB color of the background fill
     * @param borderColor the ARGB color of the 1-pixel border
     */
    public static void fillWithBorder(GuiGraphicsExtractor extractor, int x, int y,
                                      int width, int height, int fillColor, int borderColor) {
        extractor.fill(x, y, x + width, y + height, borderColor); // border background
        extractor.fill(x + 2, y + 2, x + width - 2, y + height - 2, fillColor); // inner fill
    }

    /**
     * Draws an inner shadow inside a rectangle.
     *
     * @param extractor   the GuiGraphicsExtractor instance
     * @param x           the top-left x coordinate
     * @param y           the top-left y coordinate
     * @param width       the width of the rectangle
     * @param height      the height of the rectangle
     * @param shadowColor the ARGB color of the shadow
     * @param depth       the size/depth of the shadow in pixels
     */
    public static void drawInnerShadow(GuiGraphicsExtractor extractor, int x, int y,
                                       int width, int height, int shadowColor, int depth) {
        // Top shadow
        extractor.fill(x, y, x + width, y + depth, shadowColor);
        // Bottom shadow
        extractor.fill(x, y + height - depth, x + width, y + height, shadowColor);
        // Left shadow
        extractor.fill(x, y + depth, x + depth, y + height - depth, shadowColor);
        // Right shadow
        extractor.fill(x + width - depth, y + depth, x + width, y + height - depth, shadowColor);
    }
}
