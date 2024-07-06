package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.Field;

public class ReflectField implements MySimpleReflect {

    private final Field field;
    private final String fieldName;

    public ReflectField(Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchFieldException {
        String[] $s = path.split("@");
        if (clazz == null) {
            clazz = Class.forName($s[0].replace("+", "$"));
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, $s[0].replace("+", "$"));
        }
        assert clazz != null;

        String fieldName = $s[1].replace("+", "$");
        String rename = fieldName;
        if (fieldName.contains("<")) {
            rename = fieldName.substring(fieldName.indexOf("<") + 1, fieldName.indexOf(">"));
            fieldName = fieldName.substring(0, fieldName.indexOf("<"));
        }

        this.fieldName = rename;
        field = clazz.getDeclaredField(fieldName);
        if (forceAccess) {
            field.setAccessible(true);
        }
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return fieldName;
    }

    @Override
    public String getOriginName() {
        return field.getName();
    }

    public Object execute(Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
