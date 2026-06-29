/*
 * Copyright (C) 2026  404Setup.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package one.pkg.libsl.api.ui.seeui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import one.pkg.libsl.api.ui.oreui.OreUIButton;
import one.pkg.libsl.api.ui.oreui.OreUIScrollList;
import one.pkg.libsl.api.ui.oreui.OreUITextField;
import one.pkg.libsl.api.ui.seeui.annotations.DisplayMode;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A configuration screen using OreUI components.
 */
public class OreUIConfigScreen extends OptionsSubScreen {
    private final Class<?> configClass;
    private final Runnable onSaved;
    private final String keyword;
    private final Map<String, List<OreUIConfigEntry>> categoriesMap = new LinkedHashMap<>();
    private final Component cachedTitle;
    private String activeCategory = null;
    private OreUIButton activeCategoryButton = null;

    private String aboutTitle = null;
    private String aboutCopyright = null;
    private List<String> aboutTextLines = null;

    /**
     * Creates a new OreUI configuration screen.
     *
     * @param configClass The configuration class.
     * @param lastScreen  The previous screen.
     * @param onSaved     Callback to run when settings are saved.
     */
    public OreUIConfigScreen(Class<?> configClass, Screen lastScreen, Runnable onSaved) {
        super(lastScreen, Minecraft.getInstance().options,
                Component.translatable(getKeyword(configClass) + ".config.title"));
        this.configClass = configClass;
        this.onSaved = onSaved;
        this.keyword = getKeyword(configClass);
        this.cachedTitle = this.title.copy().withStyle(Style.EMPTY.withColor(0xFFFFFFFF).withBold(true));
        loadAboutText();
    }

    /**
     * Gets the translation keyword prefix for the configuration class.
     *
     * @param clazz The configuration class.
     * @return The keyword prefix.
     */
    private static String getKeyword(Class<?> clazz) {
        one.pkg.config.annotation.config.ConfigEntry ann =
                clazz.getAnnotation(one.pkg.config.annotation.config.ConfigEntry.class);
        return ann != null ? ann.value() : "gui.kreno";
    }

    /**
     * Splits a string into lines based on maximum width, avoiding O(N^2) memory allocations.
     */
    private static List<String> splitStringByWidth(String text, Font font, int maxTextWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentLineWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLineWidth = 0;
                continue;
            }

            int charWidth = font.width(String.valueOf(c));
            if (currentLineWidth + charWidth > maxTextWidth && !currentLine.isEmpty()) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLineWidth = 0;
                if (maxLines > 0 && lines.size() >= maxLines) {
                    break;
                }
            }

            currentLine.append(c);
            currentLineWidth += charWidth;
        }
        if ((maxLines <= 0 || lines.size() < maxLines) && !currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private void loadAboutText() {
        try (InputStream in = configClass.getResourceAsStream("/META-INF/ABOUT.txt")) {
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                List<String> lines = new ArrayList<>();
                String readLine;
                while ((readLine = reader.readLine()) != null) {
                    lines.add(readLine);
                }
                aboutTextLines = new ArrayList<>();
                boolean parsedTitle = false;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (i == 0 && line.startsWith("title:")) {
                        aboutTitle = line.substring(6).trim();
                        parsedTitle = true;
                    } else if (i == 1 && parsedTitle && line.startsWith("copyright:")) {
                        aboutCopyright = line.substring(10).trim();
                    } else {
                        aboutTextLines.add(line);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void repositionElements() {
        this.rebuildWidgets();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor extractor,
                                   int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
        extractor.textRenderer().accept(
                net.minecraft.client.gui.TextAlignment.CENTER,
                this.width / 2, 20,
                this.cachedTitle
        );
    }

    @Override
    protected void init() {
        if (this.list != null) {
            this.removeWidget(this.list);
            this.list = null;
        }
        activeCategoryButton = null;
        categoriesMap.clear();
        parseConfig();

        int listWidth = Math.min(this.width - 40, 600);
        int listX = (this.width - listWidth) / 2;
        int listY = 50;
        int listHeight = this.height - listY - 50;

        int leftWidth = 150;
        int rightWidth = listWidth - leftWidth - 10;
        int leftX = listX;
        int rightX = listX + leftWidth + 10;

        OreUIScrollList categoryList = OreUIScrollList.builder().pos(leftX, listY)
                .size(leftWidth, listHeight).itemHeight(27).build();
        this.addRenderableWidget(categoryList);

        OreUIScrollList scrollList = OreUIScrollList.builder().pos(rightX, listY)
                .size(rightWidth, listHeight).itemHeight(34).build();
        this.addRenderableWidget(scrollList);

        if (activeCategory == null && !categoriesMap.isEmpty()) {
            activeCategory = categoriesMap.keySet().iterator().next();
        } else if (activeCategory == null && aboutTextLines != null) {
            activeCategory = "ABOUT";
        }

        for (String catKey : categoriesMap.keySet()) {
            Component catName = Component.translatable(keyword + ".config.category." + catKey);
            String tooltipKey = keyword + ".config.category." + catKey + ".desc";
            Component catTooltip = I18n.exists(tooltipKey) ?
                    Component.translatable(tooltipKey) : null;

            OreUIButton.Style btnStyle = catKey.equals(activeCategory) ?
                    OreUIButton.Style.CATEGORY_SELECTED : OreUIButton.Style.CATEGORY_UNSELECTED;
            var btnBuilder = OreUIButton.oreUIBuilder(catName, (btn) -> {
                activeCategory = catKey;

                if (activeCategoryButton != null) {
                    activeCategoryButton.setStyle(OreUIButton.Style.CATEGORY_UNSELECTED);
                }
                if (btn instanceof OreUIButton oreBtn) {
                    activeCategoryButton = oreBtn;
                    oreBtn.setStyle(OreUIButton.Style.CATEGORY_SELECTED);
                }

                showCategory(scrollList, activeCategory, categoriesMap.get(activeCategory),
                        rightWidth - 20);
                scrollList.setScrollAmount(0);
            }).pos(0, 0).size(leftWidth - 10, 20).style(btnStyle);

            if (catTooltip != null) {
                btnBuilder.tooltip(Tooltip.create(catTooltip));
            }

            OreUIButton catBtn = btnBuilder.build();
            if (catKey.equals(activeCategory)) {
                activeCategoryButton = catBtn;
            }
            categoryList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(catBtn));
        }

        if (aboutTextLines != null) {
            Component catName = Component.translatable("gui.about");

            OreUIButton.Style btnStyle = "ABOUT".equals(activeCategory) ?
                    OreUIButton.Style.CATEGORY_SELECTED : OreUIButton.Style.CATEGORY_UNSELECTED;
            var btnBuilder = OreUIButton.oreUIBuilder(catName, (btn) -> {
                activeCategory = "ABOUT";

                if (activeCategoryButton != null) {
                    activeCategoryButton.setStyle(OreUIButton.Style.CATEGORY_UNSELECTED);
                }
                if (btn instanceof OreUIButton oreBtn) {
                    activeCategoryButton = oreBtn;
                    oreBtn.setStyle(OreUIButton.Style.CATEGORY_SELECTED);
                }

                showAboutCategory(scrollList, rightWidth - 20);
                scrollList.setScrollAmount(0);
            }).pos(0, 0).size(leftWidth - 10, 20).style(btnStyle);

            OreUIButton catBtn = btnBuilder.build();
            if ("ABOUT".equals(activeCategory)) {
                activeCategoryButton = catBtn;
            }
            categoryList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(catBtn));
        }

        if (activeCategory != null) {
            if ("ABOUT".equals(activeCategory)) {
                showAboutCategory(scrollList, rightWidth - 20);
            } else {
                showCategory(scrollList, activeCategory, categoriesMap.get(activeCategory),
                        rightWidth - 20);
            }
        }

        OreUIButton doneBtn = OreUIButton.oreUIBuilder(CommonComponents.GUI_DONE, (_) -> {
                    if (onSaved != null) onSaved.run();
                    this.minecraft.setScreen(this.lastScreen);
                }).pos(this.width / 2 - 100, this.height - 40).size(200, 24)
                .style(OreUIButton.Style.GREEN).build();
        this.addRenderableWidget(doneBtn);
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void addFooter() {
    }

    /**
     * Parses the configuration class to identify entries and categories.
     */
    private void parseConfig() {
        List<ConfigParserCache.ParsedField> parsedFields = ConfigParserCache.getParsedFields(configClass);
        for (ConfigParserCache.ParsedField pf : parsedFields) {
            OreUIConfigEntry entry = createEntry(pf.field(), pf.group(), pf.key(), pf.comment(),
                    pf.min(), pf.max(), pf.mode(), pf.displayMode());
            if (entry != null) categoriesMap.computeIfAbsent(pf.group(),
                    (_) -> new ArrayList<>()).add(entry);
        }
    }


    private void showAboutCategory(OreUIScrollList scrollList, int entryWidth) {
        scrollList.clearScrollEntries();

        Component headerText;
        if (aboutTitle != null && !aboutTitle.isEmpty()) {
            headerText = Component.literal(aboutTitle);
        } else {
            headerText = Component.translatable("gui.about");
        }

        Component catTooltip = null;
        if (aboutCopyright != null && !aboutCopyright.isEmpty()) {
            catTooltip = Component.literal(aboutCopyright);
        }

        Font font = Minecraft.getInstance().font;
        int maxTextWidth = entryWidth - 20;

        List<String> titleLines = splitStringByWidth(headerText.getString(), font, maxTextWidth, 0);
        List<String> tooltipLines = catTooltip != null
                ? splitStringByWidth(catTooltip.getString(), font, maxTextWidth, 0)
                : List.of();

        int _totalTextHeight = 8 + (titleLines.size() - 1) * 12;
        if (!tooltipLines.isEmpty()) {
            _totalTextHeight += 6 + tooltipLines.size() * 12;
        }
        final int totalTextHeight = _totalTextHeight;
        int headerHeight = Math.max(30, totalTextHeight + 16);

        List<Component> precalculatedTitleLines = new ArrayList<>(titleLines.size());
        for (String tLine : titleLines) {
            precalculatedTitleLines.add(Component.literal(tLine).withStyle(Style.EMPTY.withColor(0xFFD0D0D0)));
        }

        List<Component> precalculatedTooltipLines = new ArrayList<>(tooltipLines.size());
        for (String line : tooltipLines) {
            precalculatedTooltipLines.add(Component.literal(line).withStyle(Style.EMPTY.withColor(0xFFA0A0A0)));
        }

        AbstractWidget headerWidget = new AbstractWidget(0, 0, entryWidth, headerHeight, headerText) {
            {
                this.active = false;
            }

            @Override
            public boolean isFocused() {
                return false;
            }

            @Override
            protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
            }

            @Override
            protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                                    int mouseX, int mouseY, float partialTick) {
                int textY = this.getY() + (this.getHeight() - totalTextHeight) / 2;

                for (Component tLine : precalculatedTitleLines) {
                    extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(TextAlignment.LEFT, this.getX() + 10, textY, tLine);
                    textY += 12;
                }

                if (!precalculatedTooltipLines.isEmpty()) {
                    textY += 2;
                    for (Component line : precalculatedTooltipLines) {
                        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                                .accept(TextAlignment.LEFT, this.getX() + 10, textY, line);
                        textY += 12;
                    }
                }
            }

            @Override
            public void setFocused(boolean focused) {
            }
        };
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(headerWidget));

        List<String> contentLines = new ArrayList<>();
        for (String line : aboutTextLines) {
            if (line.isEmpty()) {
                contentLines.add("");
            } else {
                contentLines.addAll(splitStringByWidth(line, font, maxTextWidth, 0));
            }
        }

        for (String line : contentLines) {
            Component lineComp = Component.literal(line).withStyle(Style.EMPTY.withColor(0xFFFFFFFF));
            AbstractWidget textWidget = new AbstractWidget(0, 0, entryWidth, 12, lineComp) {
                {
                    this.active = false;
                }

                @Override
                public boolean isFocused() {
                    return false;
                }

                @Override
                protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
                }

                @Override
                protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                                        int mouseX, int mouseY, float partialTick) {
                    extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(TextAlignment.LEFT, this.getX() + 10, this.getY() + 2, lineComp);
                }

                @Override
                public void setFocused(boolean focused) {
                }
            };
            scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(textWidget));
        }
    }

    /**
     * Shows the entries for a specific category in the scroll list.
     *
     * @param scrollList  The scroll list to add entries to.
     * @param categoryKey The category key.
     * @param entries     The list of configuration entries.
     * @param entryWidth  The width available for entries.
     */
    private void showCategory(OreUIScrollList scrollList, String categoryKey,
                              List<OreUIConfigEntry> entries, int entryWidth) {
        scrollList.clearScrollEntries();

        final Component headerText = Component.translatable(keyword + ".config.category." + categoryKey);
        String tooltipKey = keyword + ".config.category." + categoryKey + ".desc";
        Component catTooltip = I18n.exists(tooltipKey) ?
                Component.translatable(tooltipKey) : null;

        Font font = Minecraft.getInstance().font;
        int maxTextWidth = entryWidth - 20;

        List<String> titleLines = splitStringByWidth(headerText.getString(), font, maxTextWidth, 0);
        List<String> tooltipLines = catTooltip != null
                ? splitStringByWidth(catTooltip.getString(), font, maxTextWidth, 0)
                : List.of();

        int _totalTextHeight = 8 + (titleLines.size() - 1) * 12;
        if (!tooltipLines.isEmpty()) {
            _totalTextHeight += 6 + tooltipLines.size() * 12;
        }
        final int totalTextHeight = _totalTextHeight;
        int headerHeight = Math.max(30, totalTextHeight + 16);

        List<Component> precalculatedTitleLines = new ArrayList<>(titleLines.size());
        for (String tLine : titleLines) {
            precalculatedTitleLines.add(Component.literal(tLine).withStyle(Style.EMPTY.withColor(0xFFD0D0D0)));
        }

        List<Component> precalculatedTooltipLines = new ArrayList<>(tooltipLines.size());
        for (String line : tooltipLines) {
            precalculatedTooltipLines.add(Component.literal(line).withStyle(Style.EMPTY.withColor(0xFFA0A0A0)));
        }

        AbstractWidget headerWidget = new AbstractWidget(0, 0, entryWidth, headerHeight, headerText) {
            {
                this.active = false;
            }

            @Override
            public boolean isFocused() {
                return false;
            }

            @Override
            protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
            }

            @Override
            protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                                    int mouseX, int mouseY, float partialTick) {
                int textY = this.getY() + (this.getHeight() - totalTextHeight) / 2;

                for (Component tLine : precalculatedTitleLines) {
                    extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(TextAlignment.LEFT, this.getX() + 10, textY, tLine);
                    textY += 12;
                }

                if (!precalculatedTooltipLines.isEmpty()) {
                    textY += 2;
                    for (Component line : precalculatedTooltipLines) {
                        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                                .accept(TextAlignment.LEFT, this.getX() + 10, textY, line);
                        textY += 12;
                    }
                }
            }

            @Override
            public void setFocused(boolean focused) {
            }


        };
        scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(headerWidget));

        for (OreUIConfigEntry entry : entries) {
            AbstractWidget wrappedWidget = createWidgetWithAutoSave(entry, entryWidth);
            scrollList.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(wrappedWidget));
        }
    }

    /**
     * Creates a widget that automatically saves its value when changed.
     *
     * @param entry      The configuration entry.
     * @param entryWidth The width of the entry.
     * @return The created widget.
     */
    private AbstractWidget createWidgetWithAutoSave(OreUIConfigEntry entry, int entryWidth) {
        int compWidth = entry instanceof OreUIConfigEntry.BooleanEntry ? 36 : Math.min(200, entryWidth / 2);
        int compHeight = 24;

        AbstractWidget component = entry.createWidget(0, 0, compWidth, compHeight);
        Component tooltip = entry.getTooltip();

        int widgetHeight = 34;
        Font font = Minecraft.getInstance().font;
        int maxTextWidth = entryWidth - compWidth - 30;

        List<String> titleLines = splitStringByWidth(entry.getLabel().getString(), font, maxTextWidth, 0);
        List<String> tooltipLines = tooltip != null
                ? splitStringByWidth(tooltip.getString(), font, maxTextWidth, 3)
                : List.of();

        int _totalTextHeight = 8 + (titleLines.size() - 1) * 12;
        if (!tooltipLines.isEmpty()) {
            _totalTextHeight += 6 + tooltipLines.size() * 12;
        }
        final int totalTextHeight = _totalTextHeight;
        widgetHeight = Math.max(34, totalTextHeight + 16);

        List<Component> precalculatedTitleLines = new ArrayList<>(titleLines.size());
        for (String tLine : titleLines) {
            precalculatedTitleLines.add(Component.literal(tLine).withStyle(Style.EMPTY.withColor(0xFFFFFFFF)));
        }

        List<Component> precalculatedTooltipLines = new ArrayList<>(tooltipLines.size());
        for (String line : tooltipLines) {
            precalculatedTooltipLines.add(Component.literal(line).withStyle(Style.EMPTY.withColor(0xFFA0A0A0)));
        }

        return new AbstractWidget(0, 0, entryWidth, widgetHeight, entry.getLabel()) {

            @Override
            public void setWidth(int width) {
                super.setWidth(width);
                component.setX(this.getX() + this.getWidth() - compWidth - 10);
                component.setWidth(compWidth);
            }

            @Override
            public void setX(int x) {
                super.setX(x);
                component.setX(x + this.getWidth() - compWidth - 10);
            }

            @Override
            public void setY(int y) {
                super.setY(y);
                component.setY(y + (this.getHeight() - compHeight) / 2);
            }

            @Override
            protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor extractor,
                                                    int mouseX, int mouseY, float partialTick) {
                extractor.fill(this.getX(), this.getY() + this.getHeight() - 1,
                        this.getX() + this.getWidth(),
                        this.getY() + this.getHeight(), 0x33FFFFFF);

                int textY = this.getY() + (this.getHeight() - totalTextHeight) / 2;

                for (Component tLine : precalculatedTitleLines) {
                    extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                            .accept(TextAlignment.LEFT, this.getX() + 10, textY, tLine);
                    textY += 12;
                }

                if (!precalculatedTooltipLines.isEmpty()) {
                    textY += 2; // Extra space between title and tooltip
                    for (Component line : precalculatedTooltipLines) {
                        extractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                                .accept(TextAlignment.LEFT, this.getX() + 10, textY, line);
                        textY += 12;
                    }
                }

                component.setX(this.getX() + this.getWidth() - compWidth - 10);
                component.setY(this.getY() + (this.getHeight() - compHeight) / 2);
                component.setWidth(compWidth);
                component.setHeight(compHeight);

                component.extractRenderState(extractor, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDouble) {
                if (component.isMouseOver(event.x(), event.y())) {
                    boolean result = component.mouseClicked(event, isDouble);
                    if (result && !(component instanceof OreUITextField)) {
                        component.setFocused(false);
                        OreUIConfigScreen.this.setFocused(null);
                    } else if (component instanceof OreUITextField) {
                        this.setFocused(true);
                        OreUIConfigScreen.this.setFocused(this);
                    }
                    return result;
                }
                if (component instanceof OreUITextField) {
                    component.setFocused(false);
                }
                return super.mouseClicked(event, isDouble);
            }

            @Override
            public boolean mouseReleased(@NonNull MouseButtonEvent event) {
                return component.mouseReleased(event);
            }

            @Override
            public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
                return component.mouseDragged(event, dragX, dragY);
            }

            @Override
            public boolean isFocused() {
                return component.isFocused();
            }

            @Override
            protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
                component.updateNarration(output);
            }

            @Override
            public boolean charTyped(@NonNull CharacterEvent event) {
                return component.charTyped(event);
            }

            @Override
            public boolean keyPressed(@NonNull KeyEvent event) {
                return component.keyPressed(event);
            }

            @Override
            public boolean keyReleased(@NonNull KeyEvent event) {
                return component.keyReleased(event);
            }

            @Override
            public void setFocused(boolean focused) {
                component.setFocused(focused);
            }


        };
    }

    /**
     * Creates a configuration entry based on the field type and annotations.
     *
     * @param field       The field.
     * @param category    The category.
     * @param key         The key.
     * @param comment     The comment.
     * @param min         The minimum value.
     * @param max         The maximum value.
     * @param mode        The entry mode.
     * @param displayMode The display mode annotation.
     * @return The created configuration entry.
     */
    private OreUIConfigEntry createEntry(Field field, String category, String key,
                                         String comment, double min, double max,
                                         EntryMode mode, DisplayMode displayMode) {
        Class<?> type = field.getType();
        if (mode == EntryMode.CYCLE && displayMode != null)
            return new OreUIConfigEntry.CycleEntry(field, category, key, comment,
                    keyword, displayMode.cycleValues());
        if (mode == EntryMode.BUTTON_GROUP && displayMode != null)
            return new OreUIConfigEntry.ButtonGroupEntry(field, category, key,
                    comment, keyword, displayMode.cycleValues());
        if (mode == EntryMode.SLIDER)
            return new OreUIConfigEntry.SliderEntry(field, category, key, comment, keyword, min, max);

        if (type == boolean.class || type == Boolean.class)
            return new OreUIConfigEntry.BooleanEntry(field, category, key, comment, keyword);
        if (type == int.class || type == Integer.class)
            return new OreUIConfigEntry.IntegerEntry(field, category, key, comment, keyword, min, max);
        if (type == long.class || type == Long.class)
            return new OreUIConfigEntry.LongEntry(field, category, key, comment, keyword, min, max);
        if (type == double.class || type == Double.class || type == float.class || type == Float.class)
            return new OreUIConfigEntry.DoubleEntry(field, category, key, comment, keyword, min, max);
        return null;
    }
}
