package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectConstructor implements MySimpleReflect {
    private final String fullPath;
    private final Constructor<?> constructor;
    private final String name;

    /**
     *
     * @param clazz
     * @param path
     * @param forceAccess
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    // "a.b.c.D$DA$DAA<constructor name>(fields...)"
    public ReflectConstructor(String fullPath, Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchMethodException {
        this.fullPath = fullPath;
        String className = path.substring(0, path.indexOf('(')).replace("+", "$");
        if (className.contains("<")) {
            name = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
            className = className.substring(0, className.lastIndexOf("<"));
        } else {
            name = "init";
        }

        if (clazz == null) {
            clazz = Class.forName(className);
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, className);
        }

        path = path.substring(path.indexOf('(') + 1, path.indexOf(')'));
        if (!path.isEmpty()) {
            String[] $s = path.split(",");
            Class[] params = new Class[$s.length];
            for (int i = 0; i < $s.length; i++) {
                params[i] = MySimpleReflect.forName($s[i]);
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
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(fullPath, e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginName() {
        return name;
    }

    public String getFullPath() {
        return fullPath;
    }
}
