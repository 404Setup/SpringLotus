# SpringLotus UI

SpringLotus UI provides a set of cross-loader (Fabric/Forge/NeoForge) `OreUI`-styled components. All components support the Builder pattern.

## SpringLotus Base UI Components

### OreUIButton
A basic button component extending Minecraft's `Button`. It automatically scrolls text if it exceeds the button width.
Supported `Style`s: `NORMAL` (default), `GREEN`, `FLAT`, `CATEGORY_SELECTED`, `CATEGORY_UNSELECTED`.

```java
OreUIButton button = OreUIButton.oreUIBuilder(Component.literal("Click Me"), b -> {
    System.out.println("Clicked!");
}).pos(10, 10).size(150, 40).style(OreUIButton.Style.GREEN).build();
```

### OreUIButtonGroup
A horizontal radio button group. Used for selecting a single option among many, featuring smooth background highlight animations when toggling.

```java
OreUIButtonGroup group = OreUIButtonGroup.builder()
    .pos(0, 0).size(150, 24)
    .options(List.of(Component.literal("Option A"), Component.literal("Option B")))
    .selectedIndex(0)
    .onSelect(idx -> System.out.println("Selected: " + idx))
    .build();
```

### OreUIDropdown
A dropdown menu. It automatically calculates whether to expand upwards or downwards based on available screen space and includes a scrollbar if there are too many options.

```java
OreUIDropdown dropdown = OreUIDropdown.builder(Component.literal("Select Mode"))
    .pos(10, 10).size(150, 24)
    .options(List.of(Component.literal("Survival"), Component.literal("Creative")))
    .onSelect(index -> System.out.println("Selected: " + index))
    .build();
```

### OreUIImage
Used for rendering textures/icons.

> [!WARNING]
> This component is not fully tested; some scaling ratios might have issues.

```java
OreUIImage image = new OreUIImage(10, 10, 64, 64, new Identifier("springlotus", "textures/gui/icon.png"));
```

### OreUIScrollList
An OreUI-styled scrollable list. You can embed other `AbstractWidget`s (like buttons, text fields, etc.) using `addScrollEntry`. It handles mouse scrolling and scrollbar rendering automatically.

```java
OreUIScrollList list = OreUIScrollList.builder()
    .pos(10, 10).size(200, 150)
    .itemHeight(30)
    .build();
    
list.addScrollEntry(new OreUIScrollList.OreUIScrollListEntry(
    OreUIButton.oreUIBuilder(Component.literal("List Button"), b -> {}).size(180, 24).build()
));
```

### OreUISlider
A slider component with values ranging from `0.0 ~ 1.0`, featuring smooth transition animations.

```java
OreUISlider slider = OreUISlider.builder(Component.literal("Volume"))
    .pos(10, 10).size(200, 30).value(0.5)
    .tooltip(Tooltip.create(Component.literal("Adjust Volume")))
    .build();
```

### OreUISwitch
A boolean toggle switch with sliding animations.

```java
OreUISwitch uiSwitch = OreUISwitch.builder(Component.literal("Enable Feature"), state -> {
    System.out.println("State: " + state);
}).pos(10, 10).initialState(true).build();
```

### OreUITag
A static, non-interactive tag, perfect for status indicators (e.g., "New", "Beta"). Supports predefined colors like black, green, blue, yellow, and red.

```java
OreUITag tag = OreUITag.builder(Component.literal("New Feature"))
    .style(OreUITag.Style.RED)
    .pos(10, 10).build();
```

### OreUITextField
An input field based on the native `EditBox`. In addition to visual changes, it also supports displaying an optional title text above the field.

```java
OreUITextField textField = OreUITextField.builder(font, Component.literal("Enter nickname..."))
    .pos(10, 10).size(200, 40)
    .build();
textField.setValue("Steve");
```

## SpringLotus Composite UI (Advanced Screens)

### OreUIDialog
A modal dialog screen. When displayed, it darkens the background and supports custom text, images, and internal components. Content automatically scrolls if it exceeds the maximum height. Commonly used for confirmations or warnings.

```java
OreUIDialog dialog = new OreUIDialog(Component.literal("Warning"), this.lastScreen)
     .content(Component.literal("Are you sure you want to delete this?"))
     .confirmText(Component.literal("Confirm"))
     .cancelText(Component.literal("Cancel"))
     .onConfirm(() -> {
         System.out.println("Deleted");
     });
Minecraft.getInstance().setScreen(dialog);
```

### OreUIExampleScreen
A test and example screen containing rendering demos for all components. Useful as a reference during development.

```java
Minecraft.getInstance().setScreen(new OreUIExampleScreen(currentScreen));
```

### OreUIConfigScreen
A reflection-based automated configuration screen. Pass a config class with `ConfigEntry` annotations, and it automatically generates:
- A category list on the left (sidebar)
- Corresponding config controls on the right (Switch/Slider/TextField, etc.)
- An "About" page if `/META-INF/ABOUT.txt` is present

```java
OreUIConfigScreen configScreen = new OreUIConfigScreen(
    MyModConfig.class, // Annotated data class
    currentScreen,     // Parent screen to return to
    () -> {
        // Save config callback
        MyModConfig.save();
    }
);
Minecraft.getInstance().setScreen(configScreen);
```

