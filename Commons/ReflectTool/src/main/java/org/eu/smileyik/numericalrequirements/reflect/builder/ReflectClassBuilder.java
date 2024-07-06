package org.eu.smileyik.numericalrequirements.reflect.builder;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;
import org.eu.smileyik.numericalrequirements.reflect.ReflectField;

import java.util.*;

public class ReflectClassBuilder {
    final String name;

    List<Field> fieldList = new ArrayList<Field>();
    List<ReflectMethodBuilder> methodBuilders = new ArrayList<>();
    List<ReflectConstructorBuilder> constructorBuilders = new ArrayList<>();
    List<ReflectInnerClassBuilder> innerClassBuilders = new ArrayList<>();

    public ReflectClassBuilder(String classname) {
        this.name = classname;
    }

    public ReflectClassBuilder field(String fieldName) {
        fieldList.add(new Field(fieldName, fieldName));
        return this;
    }

    public ReflectClassBuilder field(String fieldName, String newFieldName) {
        fieldList.add(new Field(fieldName, newFieldName));
        return this;
    }

    public ReflectMethodBuilder method() {
        return new ReflectMethodBuilder(this);
    }

    public ReflectMethodBuilder method(String methodName) {
        return new ReflectMethodBuilder(this, methodName);
    }

    public ReflectMethodBuilder method(String methodName, String newMethodName) {
        return new ReflectMethodBuilder(this, methodName, newMethodName);
    }

    public ReflectConstructorBuilder constructor() {
        return new ReflectConstructorBuilder(this);
    }

    public ReflectConstructorBuilder constructor(String name) {
        return new ReflectConstructorBuilder(this, name);
    }

    public ReflectInnerClassBuilder innerClass(String name) {
        return new ReflectInnerClassBuilder(this, name);
    }

    protected void addMethodBuilder(ReflectMethodBuilder methodBuilder) {
        methodBuilders.add(methodBuilder);
    }

    protected void addConstructorBuilder(ReflectConstructorBuilder constructorBuilder) {
        constructorBuilders.add(constructorBuilder);
    }

    protected void addInnerClassBuilder(ReflectInnerClassBuilder innerClassBuilder) {
        innerClassBuilders.add(innerClassBuilder);
    }

    public ReflectClass toClass() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return MySimpleReflect.getReflectClass(toString());
    }

    public ReflectClass toClassForce() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return MySimpleReflect.getReflectClass(toString(), true);
    }

    public String toPrettyString() {
        return toPrettyPath(0);
    }

    public ReflectInnerClassBuilder toInnerClass() {
        if (this instanceof ReflectInnerClassBuilder) {
            return (ReflectInnerClassBuilder) this;
        }
        throw new ClassCastException("This class is not a ReflectInnerClassBuilder");
    }

    protected String toPrettyPath(int baseDeep) {
        ReflectClassPathBuilder builder = new ReflectClassPathBuilder(baseDeep);
        builder.newGroup(name);
        if (!fieldList.isEmpty()) {
            builder.newGroup(Field.PREFIX);
            for (Field field : fieldList) {
                builder.append(field.toStringNoPrefix());
            }
            builder.endGroup();
        }
        if (!methodBuilders.isEmpty()) {
            builder.newGroup(ReflectMethodBuilder.PREFIX);
            for (ReflectMethodBuilder methodBuilder : methodBuilders) {
                builder.append(methodBuilder.toStringNoPrefix());
            }
            builder.endGroup();
        }
        if (!constructorBuilders.isEmpty()) {
            builder.newGroup("");
            for (ReflectConstructorBuilder constructorBuilder : constructorBuilders) {
                builder.append(constructorBuilder.toString());
            }
            builder.endGroup();
        }
        if (!innerClassBuilders.isEmpty()) {
            builder.newGroup("$");
            for (ReflectInnerClassBuilder innerClassBuilder : innerClassBuilders) {
                builder.rawAppend(innerClassBuilder.toPrettyPath(builder.getDeep()));
            }
            builder.endGroup();
        }
        builder.endGroup();
        return builder.finish();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!fieldList.isEmpty()) {
            sb.append(listFormat(fieldList));
        }
        if (!constructorBuilders.isEmpty()) {
            sb.append(listFormat(constructorBuilders));
        }
        if (!methodBuilders.isEmpty()) {
            sb.append(listFormat(methodBuilders));
        }
        if (!innerClassBuilders.isEmpty()) {
            sb.append(listFormat(innerClassBuilders));
        }
        String string = String.format("%s{%s}", name, sb);
        DebugLogger.debug("build a reflect path: \n %s", string);
        return string;
    }

    private String listFormat(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o).append(";");
        }
        return String.format("{%s}", sb.isEmpty() ? "" : sb.substring(0, sb.length() - 1));
    }

    private static final class Field {
        static final String PREFIX = "@";

        final String name;
        final String newName;

        private Field(String name, String newName) {
            this.name = name;
            this.newName = newName;
        }

        @Override
        public String toString() {
            return PREFIX + toStringNoPrefix();
        }

        public String toStringNoPrefix() {
            return String.format("%s<%s>", name, newName);
        }
    }
}
