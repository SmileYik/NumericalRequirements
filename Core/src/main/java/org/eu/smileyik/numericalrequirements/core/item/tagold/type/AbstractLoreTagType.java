package org.eu.smileyik.numericalrequirements.core.item.tagold.type;

import java.util.Objects;

public abstract class AbstractLoreTagType<T> implements LoreTagType<T> {
    private final String typeName;
    private final String regex;

    public AbstractLoreTagType(String typeName, String regex) {
        this.typeName = typeName;
        this.regex = regex;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean matches(String value) {
        return value.matches(regex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLoreTagType<?> that = (AbstractLoreTagType<?>) o;
        return Objects.equals(typeName, that.typeName) && Objects.equals(regex, that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, regex);
    }

    @Override
    public String toString() {
        return "AbstractLoreTagType{" +
                "typeName='" + typeName + '\'' +
                ", regex='" + regex + '\'' +
                '}';
    }
}
