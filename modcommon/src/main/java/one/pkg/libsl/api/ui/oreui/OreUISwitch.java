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
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * A native Minecraft switch component styled to look like an OreUI switch.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * OreUISwitch uiSwitch = OreUISwitch.builder(Component.literal("Toggle Me"), state -> {
 *     System.out.println("Switch is now " + (state ? "ON" : "OFF"));
 * }).pos(10, 10).initialState(true).build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class OreUISwitch extends AbstractWidget {

    private final Consumer<Boolean> onToggle;
    private boolean state;
    private float animationProgress = -1.0f;
    private Component cachedMessageActive;
    private Component cachedMessageInactive;

    /**
     * Constructs a new {@code OreUISwitch}.
     *
     * @param x            The x-position.
     * @param y            The y-position.
     * @param width        The width.
     * @param height       The height.
     * @param message      The message to display.
     * @param initialState The initial state.
     * @param onToggle     The callback when the switch is toggled.
     */
    protected OreUISwitch(int x, int y, int width, int height, Component message,
                          boolean initialState, Consumer<Boolean> onToggle) {
        super(x, y, width, height, message);
        this.state = initialState;
        this.onToggle = onToggle;
        updateCachedMessages();
    }

    /**
     * Creates a new builder for an {@code OreUISwitch}.
     *
     * @param message  The message to display.
     * @param onToggle The callback when the switch is toggled.
     * @return A new {@link Builder} instance.
     */
    public static Builder builder(Component message, Consumer<Boolean> onToggle) {
        return new Builder(message, onToggle);
    }

    @Override
    public void setMessage(@NotNull Component message) {
        super.setMessage(message);
        updateCachedMessages();
    }

    private void updateCachedMessages() {
        Component msg = getMessage();
        this.cachedMessageActive = msg.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
        this.cachedMessageInactive = msg.copy().withStyle(Style.EMPTY.withColor(0xFF48494A));
    }

    /**
     * Gets the current state of the switch.
     *
     * @return {@code true} if ON, {@code false} if OFF.
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets the state of the switch.
     *
     * @param state The new state.
     */
    public void setState(boolean state) {
        if (this.state != state) {
            this.state = state;
            if (this.onToggle != null) {
                this.onToggle.accept(this.state);
            }
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean isDouble) {
        if (this.active) {
            this.setState(!this.state);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }

    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                         int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        boolean disabled = !active;
        boolean hovered = isHoveredOrFocused() || isHovered();

        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                .accept(TextAlignment.LEFT, x,
                        y + (height - 8) / 2,
                        disabled ? cachedMessageInactive : cachedMessageActive);

        int switchWidth = 24;
        int switchX = x + width - switchWidth;

        int borderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;

        if (this.animationProgress == -1.0f) {
            this.animationProgress = state ? 1.0f : 0.0f;
        }
        float targetProgress = state ? 1.0f : 0.0f;
        this.animationProgress = net.minecraft.util.Mth.lerp(0.4f, this.animationProgress, targetProgress);
        if (Math.abs(this.animationProgress - targetProgress) < 0.01f) {
            this.animationProgress = targetProgress;
        }

        int trackHeight = 12;
        int trackY = y + (height - trackHeight) / 2;

        extractor.fill(switchX, trackY, switchX + switchWidth, trackY + trackHeight, borderColor);

        int thumbWidth = 10;
        int thumbHeight = 10;
        float minThumbX = switchX + 1;
        float maxThumbX = switchX + switchWidth - thumbWidth - 1;
        int thumbX = (int) (minThumbX + this.animationProgress * (maxThumbX - minThumbX));
        int thumbY = trackY + 1;

        int splitX = thumbX + thumbWidth / 2;

        if (disabled) {
            extractor.fill(switchX + 1, trackY + 1, switchX + switchWidth - 1,
                    trackY + trackHeight - 1, 0xFFD0D1D4);
        } else {
            extractor.fill(switchX + 1, trackY + 1, splitX,
                    trackY + trackHeight - 1, 0xFF3C8527);
            extractor.fill(splitX, trackY + 1, switchX + switchWidth - 1,
                    trackY + trackHeight - 1, 0xFF8C8D90);
        }

        int thumbBorderColor = disabled ? 0xFF8C8D90 : 0xFF1E1E1F;
        int thumbBgColor = disabled ? 0xFFD0D1D4 : (hovered ? 0xFFB1B2B5 : 0xFFD0D1D4);

        extractor.fill(thumbX, thumbY, thumbX + thumbWidth,
                thumbY + thumbHeight, thumbBorderColor);
        extractor.fill(thumbX + 1, thumbY + 1, thumbX + thumbWidth - 1,
                thumbY + thumbHeight - 1, thumbBgColor);

        if (!disabled) {
            extractor.fill(thumbX + 1, thumbY + thumbHeight - 2,
                    thumbX + thumbWidth - 1, thumbY + thumbHeight - 1, 0xFF58585A);
        } else {
            extractor.fill(thumbX + 1, thumbY + thumbHeight - 2,
                    thumbX + thumbWidth - 1, thumbY + thumbHeight - 1, 0xFFB1B2B5);
        }
    }

    /**
     * A builder class for {@link OreUISwitch}.
     */
    public static class Builder {
        private final Component message;
        private final Consumer<Boolean> onToggle;
        private final int width = 36;
        private final int height = 24;
        private int x = 0;
        private int y = 0;
        private boolean initialState = false;
        private Tooltip tooltip;

        /**
         * Constructs a new {@code Builder}.
         *
         * @param message  The message to display.
         * @param onToggle The callback when the switch is toggled.
         */
        public Builder(Component message, Consumer<Boolean> onToggle) {
            this.message = message;
            this.onToggle = onToggle;
        }

        /**
         * Sets the position of the switch.
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
         * Sets the initial state of the switch.
         *
         * @param state The initial state.
         * @return This builder instance.
         */
        public Builder initialState(boolean state) {
            this.initialState = state;
            return this;
        }

        /**
         * Sets the tooltip for the switch.
         *
         * @param tooltip The tooltip.
         * @return This builder instance.
         */
        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Builds the {@link OreUISwitch}.
         *
         * @return A new {@link OreUISwitch} instance.
         */
        public OreUISwitch build() {
            OreUISwitch switchWidget = new OreUISwitch(x, y, width, height, message, initialState, onToggle);
            if (this.tooltip != null) {
                switchWidget.setTooltip(this.tooltip);
            }
            return switchWidget;
        }
    }
}
