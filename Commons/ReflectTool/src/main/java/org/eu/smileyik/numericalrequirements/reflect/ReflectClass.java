package org.eu.smileyik.numericalrequirements.reflect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectClass implements MySimpleReflect {
    private final Map<String, ReflectConstructor> constructors = new HashMap<>();
    private final Map<String, ReflectField> fields = new HashMap<>();
    private final Map<String, ReflectMethod<?>> methods = new HashMap<>();

    protected ReflectClass(List<MySimpleReflect> reflects) {
        for (MySimpleReflect it : reflects) {
            if (it instanceof ReflectMethod<?>) {
                methods.put(it.getName(), (ReflectMethod<?>) it);
            } else if (it instanceof ReflectField) {
                fields.put(it.getName(), (ReflectField) it);
            } else if (it instanceof ReflectConstructor) {
                constructors.put(it.getName(), (ReflectConstructor) it);
            }
        }
    }

    public Object newInstance(String constructor, Object... args) {
        return constructors.get(constructor).execute(args);
    }

    public Object execute(String method, Object instance, Object... args) {
        return methods.get(method).execute(instance, args);
    }

    public Object get(String field, Object instance) {
        return fields.get(field).execute(instance);
    }

    public void set(String field, Object instance, Object value) {
        fields.get(field).set(instance, value);
    }

    public ReflectField getField(String field) {
        return fields.get(field);
    }

    public <T> ReflectMethod<T> getMethod(String method) {
        return (ReflectMethod<T>) methods.get(method);
    }

    public ReflectConstructor getConstructor(String constructor) {
        return constructors.get(constructor);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginName() {
        return getName();
    }
}
