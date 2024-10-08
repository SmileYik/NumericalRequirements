package org.eu.smileyik.numericalrequirements.reflect;

import java.lang.reflect.Field;

public class ReflectField implements MySimpleReflect {
    private final String fullPath;
    private final Field field;
    private final String fieldName;

    public ReflectField(String fullPath, Class<?> clazz, String path, boolean forceAccess) throws ClassNotFoundException, NoSuchFieldException {
        this.fullPath = fullPath;
        String[] $s = path.split("@");
        if (clazz == null) {
            clazz = Class.forName($s[0].replace("+", "$"));
        } else {
            clazz = MySimpleReflect.getClassInClass(clazz, $s[0].replace("+", "$"));
        }
        assert clazz != null;

        String fieldName = $s[1].replace("+", "$");
        String rename = fieldName;
        String targetType = null;
        if (fieldName.contains("<")) {
            rename = fieldName.substring(fieldName.indexOf("<") + 1, fieldName.indexOf(">"));
            fieldName = fieldName.substring(0, fieldName.indexOf("<"));
            if (rename.contains("|")) {
                String[] split = rename.split("\\|");
                rename = split[0];
                targetType = split[1];
            }
        }

        this.fieldName = rename;
        field = clazz.getDeclaredField(fieldName);

        if (targetType != null && !field.getType().getName().equals(targetType)) {
            throw new TypeNotMatchException(field.getType().getName(), targetType);
        }

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
            throw new RuntimeException(fullPath, e);
        }
    }

    public void set(Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(fullPath, e);
        }
    }

    public String getFullPath() {
        return fullPath;
    }
}
