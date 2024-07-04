package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.Field;

public class ReflectField implements MySimpleReflect {

    private final Field field;

    public ReflectField(Class<?> clazz, String path) throws ClassNotFoundException, NoSuchFieldException {
        String[] $s = path.split("@");
        if (clazz == null) {
            clazz = Class.forName($s[0].replace("+", "$"));
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, $s[0].replace("+", "$"));
        }
        field = clazz.getDeclaredField($s[1].strip().replace("+", "$"));
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Object execute(Object install) {
        try {
            return field.get(install);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
