package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectConstructor implements MySimpleReflect {
    private final Constructor<?> constructor;

    public ReflectConstructor(Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchMethodException {
        String className = path.substring(0, path.indexOf('(')).strip().replace("+", "$");
        if (clazz == null) {
            clazz = Class.forName(className);
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, className);
        }

        path = path.substring(path.indexOf('(') + 1, path.indexOf(')')).strip();
        if (!path.isEmpty()) {
            String[] $s = path.split(",");
            Class[] params = new Class[$s.length];
            for (int i = 0; i < $s.length; i++) {
                params[i] = MySimpleReflect.forName($s[i].strip());
            }
            constructor = clazz.getDeclaredConstructor(params);
        } else {
            constructor = clazz.getDeclaredConstructor();
        }

        if (forceAccess) {
            constructor.setAccessible(true);
        }
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Object execute(Object ... args) {
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
