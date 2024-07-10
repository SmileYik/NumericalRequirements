package org.eu.smileyik.numericalrequirements.core.api.item.tag.lore;

import org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator.IntTranslator;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator.NumberF1Translator;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator.NumberTranslator;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator.StringTranslator;

import java.util.HashMap;
import java.util.Map;

public interface ValueTranslator<T> {

    Map<String, ValueTranslator<?>> VALUE_TRANSLATOR_MAP = init();

    static void addValueTranslator(ValueTranslator<?> translator) {
        VALUE_TRANSLATOR_MAP.put(translator.getName(), translator);
    }

    private static Map<String, ValueTranslator<?>> init() {
        Map<String, ValueTranslator<?>> map = new HashMap<>();
        addValueTranslator(map, new IntTranslator());
        addValueTranslator(map, new NumberTranslator());
        addValueTranslator(map, new NumberF1Translator());
        addValueTranslator(map, new StringTranslator());
        return map;
    }

    private static void addValueTranslator(Map<String, ValueTranslator<?>> map, ValueTranslator<?> translator) {
        map.put(translator.getName(), translator);
    }

    /**
     * 获取类型名。
     * @return 类型名
     */
    String getName();

    /**
     * 获取正则表达式。
     * @return 正则表达式
     */
    String getRegex();

    /**
     * 检测文本是否与正则表达式匹配。
     * @param str 需要检测的文本。
     * @return
     */
    default boolean matches(String str) {
        return str != null && str.matches(getRegex());
    }

    /**
     * 将符合正则表达式的文本转换为类型 T
     * @param str 符合正则表达式的文本。
     * @return
     */
    T cast(String str);

    /**
     * 将值转换为文本。
     * @param value 值。
     * @return
     */
    default String asString(T value) {
        return value.toString();
    }

    Class<T> getTargetClass();

    /**
     * 重新格式化字符串。
     * @param value
     * @return
     */
    default String format(String value) {
        return asString(cast(value));
    }
}
