package org.eu.smileyik.numericalrequirements.luaeffect;

import org.bukkit.*;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;
import tk.smileyik.luainminecraftbukkit.util.luahelper.LuaHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class LuaConfigTool extends LuaHelper {
    public static final Class<Particle> Particle = org.bukkit.Particle.class;
    public static final Class<EntityEffect> EntityEffect = org.bukkit.EntityEffect.class;
    public static final Class<Effect> Effect = org.bukkit.Effect.class;
    public static final Class<?> Instrument = org.bukkit.Instrument.class;
    public static final Class<?> Note = org.bukkit.Note.class;
    public static final Class<?> Tone = org.bukkit.Note.Tone.class;
    public static final Class<?> Material = org.bukkit.Material.class;
    public static final Class<?> PotionEffectType = org.bukkit.potion.PotionEffectType.class;
    public static final Class<?> Color = org.bukkit.Color.class;

    public static Class<?> getClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz);
    }

    public static Object newInstance(String clazz, String[] classes, Object[] objects) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return newInstance(
                Class.forName(clazz), classes, objects
        );
    }

    public static Object newInstance(Class<?> clazz, Class[] classes, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(classes);
        return declaredConstructor.newInstance(objects);
    }

    public static Object newInstance(Class<?> clazz, String[] classes, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class[] cles = new Class[classes.length];
        for (int i = 0; i < classes.length; ++i) {
            switch (classes[i]) {
                case "double":
                    cles[i] = double.class;
                    break;
                case "int":
                    cles[i] = int.class;
                    break;
                case "float":
                    cles[i] = float.class;
                    break;
                case "short":
                    cles[i] = short.class;
                    break;
                case "byte":
                    cles[i] = byte.class;
                    break;
                default:
                    cles[i] = Class.forName(classes[i]);
                    break;
            }
            if (objects[i] instanceof Double) {
                Number obj = (Number) objects[i];

                if (cles[i] == double.class || cles[i] == Double.class) {
                    objects[i] = obj.doubleValue();
                } else if (cles[i] == float.class || cles[i] == Float.class) {
                    objects[i] = obj.floatValue();
                } else if (cles[i] == int.class || cles[i] == Integer.class) {
                    objects[i] = obj.intValue();
                } else if (cles[i] == short.class || cles[i] == Short.class) {
                    objects[i] = obj.shortValue();
                } else if (cles[i] == byte.class || cles[i] == Byte.class) {
                    objects[i] = obj.byteValue();
                }
            } else {
                objects[i] = cles[i].cast(objects[i]);
            }

        }
        return newInstance(clazz, cles, objects);
    }

    public final Class<?> getInnerClass(Class<?> parent, String className) {
        Class<?>[] declaredClasses = parent.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (declaredClass.getSimpleName().equalsIgnoreCase(className)) {
                return declaredClass;
            }
        }
        return null;
    }

    public static void print(Object obj) {
        System.out.println(obj);
    }

    public static void print(Object[] obj) {
        System.out.println(Arrays.toString(obj));
    }
}
