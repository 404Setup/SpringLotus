package one.pkg.libsl.api.ui.seeui;

import one.pkg.config.annotation.config.ConfigTarget;
import one.pkg.libsl.api.ui.seeui.annotations.DisplayMode;
import one.pkg.libsl.api.ui.seeui.annotations.Entry;
import one.pkg.libsl.api.ui.seeui.annotations.Range;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for configuration parsing to avoid repeated reflection overhead.
 */
class ConfigParserCache {
    private static final Map<Class<?>, List<ParsedField>> CACHE = new ConcurrentHashMap<>();

    /**
     * Parses and caches the configuration fields for a given configuration class.
     *
     * @param configClass The configuration class.
     * @return A list of parsed fields containing reflection and annotation data.
     */
    public static List<ParsedField> getParsedFields(Class<?> configClass) {
        return CACHE.computeIfAbsent(configClass, cls -> {
            List<ParsedField> parsedFields = new ArrayList<>();
            for (Field field : cls.getDeclaredFields()) {
                ConfigTarget target = field.getAnnotation(ConfigTarget.class);
                if (target != null) {
                    String[] groups = target.group();
                    String group = groups.length > 0 ? groups[0] : "general";
                    String key = target.value().isEmpty() ? field.getName() : target.value();
                    String comment = target.comment();

                    Entry entryAnn = field.getAnnotation(Entry.class);
                    if (entryAnn != null) group = entryAnn.category();

                    Range range = field.getAnnotation(Range.class);
                    double min = range != null ? range.min() : Double.NEGATIVE_INFINITY;
                    double max = range != null ? range.max() : Double.POSITIVE_INFINITY;

                    DisplayMode displayMode = field.getAnnotation(DisplayMode.class);
                    EntryMode mode = displayMode != null ? displayMode.value() : EntryMode.TEXT;

                    parsedFields.add(new ParsedField(field, group, key, comment, min, max, mode, displayMode));
                }
            }
            return List.copyOf(parsedFields);
        });
    }

    public record ParsedField(Field field, String group, String key, String comment, double min, double max,
                              EntryMode mode, DisplayMode displayMode) {
    }
}
