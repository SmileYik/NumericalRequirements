package org.eu.smileyik.numericalrequirements.core.item.tag.type;

public interface LoreTagType <T> {
    String getTypeName();
    String getRegex();
    T castValue(String value);
    String castString(T value);
    boolean matches(String value);
    default String castString(String value) {
        return castString(castValue(value));
    }
}
