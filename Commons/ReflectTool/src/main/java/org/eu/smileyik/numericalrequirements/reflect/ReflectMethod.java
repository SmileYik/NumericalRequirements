package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectMethod <Ret> implements MySimpleReflect {
    // org.eu.smileyik.numericalrequirements.reflect.ReflectMethod#hello(java.lang.String)
    private final String fullPath;
    private final Method method;
    private final String methodName;

    public ReflectMethod(String fullPath, Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchMethodException {
        this.fullPath = fullPath;
        String[] $s = path.split("#");
        if (clazz == null) {
            clazz = Class.forName($s[0].replace("+", "$"));
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, $s[0].replace("+", "$"));
        }

        $s[1] = $s[1].replace("+", "$");
        String methodName = $s[1].substring(0, $s[1].indexOf('('));
        $s[1] = $s[1].substring($s[1].indexOf('(') + 1, $s[1].indexOf(')'));

        String rename = methodName;
        if (methodName.contains("<")) {
            rename = methodName.substring(methodName.indexOf("<") + 1, methodName.indexOf(">"));
            methodName = methodName.substring(0, methodName.indexOf('<'));
        }
        this.methodName = rename;

        if (!$s[1].isEmpty()) {
            $s = $s[1].split(",");
            Class[] params = new Class[$s.length];
            for (int i = 0; i < $s.length; i++) {
                params[i] = MySimpleReflect.forName($s[i]);
            }
            method = clazz.getDeclaredMethod(methodName, params);
        } else {
            method = clazz.getDeclaredMethod(methodName);
        }
        if (forceAccess) {
            method.setAccessible(true);
        }
    }

    @Override
    public String getName() {
        return methodName;
    }

    public String getOriginName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    public Ret execute(Object instance, Object ... args) {
        try {
            return (Ret) method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(fullPath, e);
        }
    }

    public String getFullPath() {
        return fullPath;
    }
}
