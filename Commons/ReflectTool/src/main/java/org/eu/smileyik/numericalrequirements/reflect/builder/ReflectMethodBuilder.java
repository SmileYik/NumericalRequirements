package org.eu.smileyik.numericalrequirements.reflect.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectMethodBuilder {
    public static final String PREFIX = "#";

    private final ReflectClassBuilder parent;
    String name, newName, description;
    List<String> params = new ArrayList<>();

    protected ReflectMethodBuilder(ReflectClassBuilder parent) {
        this(parent, null, null);
    }

    protected ReflectMethodBuilder(ReflectClassBuilder parent, String name) {
        this(parent, name, name);
    }

    protected ReflectMethodBuilder(ReflectClassBuilder parent, String name, String newName) {
        this.parent = parent;
        this.name = name;
        this.newName = newName;
    }

    public ReflectMethodBuilder name(String name) {
        this.name = name;
        this.newName = name;
        return this;
    }

    public ReflectMethodBuilder name(String name, String newName) {
        this.name = name;
        this.newName = newName;
        return this;
    }

    public ReflectMethodBuilder arg(String arg) {
        params.add(arg);
        return this;
    }

    public ReflectClassBuilder args(String... params) {
        this.params = Arrays.asList(params);
        return finished();
    }

    public ReflectMethodBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ReflectClassBuilder finished() {
        if (name == null || params == null) {
            throw new IllegalArgumentException("name and params cannot be null");
        }
        parent.addMethodBuilder(this);
        return parent;
    }

    @Override
    public String toString() {
        return PREFIX + toStringNoPrefix();
    }

    public String toStringNoPrefix() {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param).append(", ");
        }
        return String.format(
                "%s<%s>(%s)%s", name, newName,
                sb.length() == 0 ? "" : sb.substring(0, sb.length() - 2),
                description == null ? "" : (" // " + description)
        );
    }
}
