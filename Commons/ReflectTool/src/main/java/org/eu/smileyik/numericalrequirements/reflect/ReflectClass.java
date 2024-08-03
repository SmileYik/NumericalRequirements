package org.eu.smileyik.numericalrequirements.reflect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectClass implements MySimpleReflect {
    private final Map<String, ReflectConstructor> constructors = new HashMap<>();
    private final Map<String, ReflectField> fields = new HashMap<>();
    private final Map<String, ReflectMethod<?>> methods = new HashMap<>();
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Class<?> mainClass;

    protected ReflectClass(List<MySimpleReflect> reflects) {
        Class<?> mainClass = null;
        for (MySimpleReflect it : reflects) {
            if (it instanceof ReflectMethod<?>) {
                methods.put(it.getName(), (ReflectMethod<?>) it);
                Class<?> declaringClass = ((ReflectMethod<?>) it).getMethod().getDeclaringClass();
                classes.put(it.getName(), declaringClass);
                if (mainClass == null) mainClass = declaringClass;
            } else if (it instanceof ReflectField) {
                fields.put(it.getName(), (ReflectField) it);
                Class<?> declaringClass = ((ReflectField) it).getField().getDeclaringClass();
                classes.put(it.getName(), declaringClass);
                if (mainClass == null) mainClass = declaringClass;
            } else if (it instanceof ReflectConstructor) {
                constructors.put(it.getName(), (ReflectConstructor) it);
                Class<?> declaringClass = ((ReflectConstructor) it).getConstructor().getDeclaringClass();
                classes.put(it.getName(), declaringClass);
                if (mainClass == null) mainClass = declaringClass;
            }
        }
        this.mainClass = mainClass;
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

    public boolean hasConstructor(String constructor) {
        return constructors.containsKey(constructor);
    }

    public boolean hasField(String field) {
        return fields.containsKey(field);
    }

    public boolean hasMethod(String method) {
        return methods.containsKey(method);
    }

    public Class<?> getClass(String className) {
        return classes.get(className);
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    @Override
    public String getName() {
        return mainClass.getName();
    }

    @Override
    public String getOriginName() {
        return getName();
    }
}
