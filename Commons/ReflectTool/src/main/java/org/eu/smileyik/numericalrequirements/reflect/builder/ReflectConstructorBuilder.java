package org.eu.smileyik.numericalrequirements.reflect.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectConstructorBuilder {
    private final ReflectClassBuilder parent;
    String name;
    List<String> params = new ArrayList<>();
    private String description = null;

    protected ReflectConstructorBuilder(ReflectClassBuilder parent) {
        this.parent = parent;
    }

    protected ReflectConstructorBuilder(ReflectClassBuilder parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public ReflectConstructorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ReflectConstructorBuilder arg(String arg) {
        params.add(arg);
        return this;
    }

    public ReflectClassBuilder args(String... params) {
        this.params = Arrays.asList(params);
        return finished();
    }

    public ReflectConstructorBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ReflectClassBuilder finished() {
        if (name == null || params == null) {
            throw new IllegalArgumentException("name and params cannot be null");
        }
        parent.addConstructorBuilder(this);
        return parent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param).append(", ");
        }
        return String.format(
                "<%s>(%s)%s", name,
                sb.length() == 0 ? "" : sb.substring(0, sb.length() - 2),
                description == null ? "" : ( " // " + description )
        );
    }
}
