package org.eu.smileyik.numericalrequirements.core.network;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class PacketToString {
    private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();


    public static String toString(Packet packet) {
        return toString(packet.getInstance());
    }

    public static String toString(Object packet) {
        try {
            return toString(packet.getClass(), packet, new HashSet<>());
        } catch (IllegalAccessException e) {
            DebugLogger.debug(e);
            return "";
        }
    }

    private static String toString(Class<?> clazz, Object obj, Set<Object> checked) throws IllegalAccessException {
        if (clazz.getSimpleName().contains("BlockStateList")) return "[blocks]";
        if (checked.contains(obj)) return "";
        if (clazz.isEnum() || Enum.class.isAssignableFrom(clazz)) return obj.toString();
        // if (aClass.isArray()) return Arrays.toString(Array.obj);
        if (!fieldCache.containsKey(clazz)) {
            Class<?> next = clazz;
            List<Field> fields = new ArrayList<>();
            while (next != null) {
                for (Field field : next.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    field.setAccessible(true);
                    fields.add(field);
                }
                next = next.getSuperclass();
            }
            fieldCache.put(clazz, fields);
        }
        List<Field> fields = fieldCache.get(clazz);

        List<String> strings = new ArrayList<>();
        for (Field field : fields) {
            Object o = field.get(obj);
            String oStr = o == null ? "null" : (o.getClass().getName().contains("minecraft") ? toString(o.getClass(), o, checked) : String.valueOf(o));
            strings.add(String.format("%s: %s", field.getName(), oStr));
            checked.add(obj);
        }
        return String.format("%s{ %s }", clazz.getSimpleName(), String.join(", ", strings));
    }
}
