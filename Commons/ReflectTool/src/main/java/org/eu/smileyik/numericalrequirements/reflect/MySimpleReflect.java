package org.eu.smileyik.numericalrequirements.reflect;

public interface MySimpleReflect {
    static <T extends MySimpleReflect> T get(String path) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        path = path.replace("\\$", "+");
        String[] $s = path.split("\\\\$");
        String classAndOther;
        Class<?> targetClass = null;
        if ($s.length != 1) {
            targetClass = Class.forName($s[0].strip().replace("+", "$"));
            for (int i = 1; i < $s.length - 1; i++) {
                targetClass = getClassInClass(targetClass, $s[i].strip().replace("+", "$"));
            }
            classAndOther = $s[$s.length - 1];
        } else {
            classAndOther = $s[0];
        }

        if (classAndOther.contains("@")) {
            return (T) new ReflectField(targetClass, classAndOther);
        } else {
            return (T) new ReflectMethod<>(targetClass, classAndOther);
        }
    }

    static Class<?> forName(String className) throws ClassNotFoundException {
        switch (className) {
            case "boolean": return boolean.class;
            case "byte": return byte.class;
            case "char": return char.class;
            case "short": return short.class;
            case "int": return int.class;
            case "long": return long.class;
            case "float": return float.class;
            case "double": return double.class;
            case "void": return void.class;
            case "boolean[]": return boolean[].class;
            case "byte[]": return byte[].class;
            case "char[]": return char[].class;
            case "short[]": return short[].class;
            case "int[]": return int[].class;
            case "long[]": return long[].class;
            case "float[]": return float[].class;
            case "double[]": return double[].class;
            default: return Class.forName(className);
        }
    }

    static Class<?> getClassInClass(Class<?> clazz, String className) {
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            if (declaredClass.getName().equals(className)) {
                return declaredClass;
            }
        }
        return null;
    }
}
