package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ReflectMethod <Ret> implements MySimpleReflect {
    // org.eu.smileyik.numericalrequirements.reflect.ReflectMethod#hello(java.lang.String)

    private final Method method;

    public ReflectMethod(Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchMethodException {
        String[] $s = path.split("#");
        if (clazz == null) {
            clazz = Class.forName($s[0].replace("+", "$"));
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, $s[0].replace("+", "$"));
        }

        $s[1] = $s[1].replace("+", "$");
        String methodName = $s[1].substring(0, $s[1].indexOf('(')).strip();
        $s[1] = $s[1].substring($s[1].indexOf('(') + 1, $s[1].indexOf(')')).strip();
        if (!$s[1].isEmpty()) {
            $s = $s[1].split(",");
            Class[] params = new Class[$s.length];
            for (int i = 0; i < $s.length; i++) {
                params[i] = MySimpleReflect.forName($s[i].strip());
            }
            method = clazz.getDeclaredMethod(methodName, params);
        } else {
            method = clazz.getDeclaredMethod(methodName);
        }
        if (forceAccess) {
            method.setAccessible(true);
        }
    }

    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Ret execute(Object instance, Object ... args) {
        try {
            return (Ret) method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
