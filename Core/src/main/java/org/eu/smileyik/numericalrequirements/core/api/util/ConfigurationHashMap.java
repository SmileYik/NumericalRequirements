package org.eu.smileyik.numericalrequirements.core.api.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationHashMap extends HashMap<String, Object> {
    public ConfigurationHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ConfigurationHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ConfigurationHashMap() {
    }

    public ConfigurationHashMap(Map<? extends String, ?> m) {
        super(m);
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return super.remove(key);
        }
        if (isValidValue(value)) {
            return super.put(key, value);
        }
        return null;
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        if (value == null) {
            return super.remove(key);
        }
        if (isValidValue(value)) {
            return super.putIfAbsent(key, value);
        }
        return null;
    }

    public ConfigurationHashMap createMap(String key) {
        ConfigurationHashMap configurationHashMap = new ConfigurationHashMap();
        this.put(key, configurationHashMap);
        return configurationHashMap;
    }

    public boolean contains(String key) {
        return super.containsKey(key);
    }

    private boolean isValidValue(@NotNull Object value) {
        return value instanceof Number ||
                value instanceof String ||
                value instanceof Boolean ||
                value instanceof ConfigurationHashMap ||
                value instanceof List;
    }

    public boolean isMap(String key) {
        return containsKey(key) && super.get(key) instanceof ConfigurationHashMap;
    }

    public ConfigurationHashMap getMap(String key) {
        return (ConfigurationHashMap) super.get(key);
    }

    public Number getNumber(String key) {
        return getNumber(key, 0);
    }

    public Number getNumber(String key, Number defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : (Number) value;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean b = (Boolean) get(key);
        return b == null ? defaultValue : b;
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.doubleValue();
    }

    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public float getFloat(String key, float defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.floatValue();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.intValue();
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.longValue();
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(String key, byte defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.byteValue();
    }

    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short defaultValue) {
        Number number = getNumber(key);
        return number == null ? defaultValue : number.shortValue();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : value.toString();
    }

    public String getColorString(String key) {
        return getColorString(key, null);
    }

    public String getColorString(String key, String defaultValue) {
        String string = getString(key, null);
        if (string == null) string = defaultValue;
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<?> getList(String key) {
        Object o = get(key);
        return (!(o instanceof List)) ? null : (List<?>) o;
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        List<?> list = getList(key);
        if (list == null) return new ArrayList<>();
        if (clazz.isAssignableFrom(list.get(0).getClass())) {
            List<T> result = new ArrayList<>();
            for (Object o : list) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return new ArrayList<>();
    }

    public List<String> getStringList(String key) {
        return getList(key, String.class);
    }

    public List<Integer> getIntegerList(String key) {
        return getList(key, Integer.class);
    }

    public List<Long> getLongList(String key) {
        return getList(key, Long.class);
    }

    public List<Byte> getByteList(String key) {
        return getList(key, Byte.class);
    }

    public List<Short> getShortList(String key) {
        return getList(key, Short.class);
    }

    public List<Double> getDoubleList(String key) {
        return getList(key, Double.class);
    }

    public List<Float> getFloatList(String key) {
        return getList(key, Float.class);
    }
}
