package org.eu.smileyik.numericalrequirements.reflect.builder;

public class ReflectInnerClassBuilder extends ReflectClassBuilder {
    public static final String PREFIX = "$";
    private final ReflectClassBuilder parent;

    public ReflectInnerClassBuilder(ReflectClassBuilder parent, String name) {
        super(name);
        this.parent = parent;
    }

    public ReflectClassBuilder finished() {
        parent.addInnerClassBuilder(this);
        return parent;
    }

    @Override
    public String toString() {
        return PREFIX + toStringNoPrefix();
    }

    public String toStringNoPrefix() {
        return super.toString();
    }
}
