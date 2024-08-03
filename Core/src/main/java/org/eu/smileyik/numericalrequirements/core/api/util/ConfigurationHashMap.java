package org.eu.smileyik.numericalrequirements.core.api.util;

import com.google.gson.*;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationHashMap extends HashMap<String, Object> {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ConfigurationHashMap.class, (JsonDeserializer<ConfigurationHashMap>) (json, typeOfT, context) -> {
                if (json instanceof JsonObject) {
                    ConfigurationHashMap map = new ConfigurationHashMap();
                    for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                        JsonElement value = entry.getValue();
                        Class<?> clazz = value instanceof JsonObject ? ConfigurationHashMap.class : Object.class;
                        map.putWithoutCheck(entry.getKey(), context.deserialize(value, clazz));
                    }
                    return map;
                } else {
                    return context.deserialize(json, Object.class);
                }
            }).create();
    private ConfigurationHashMap parent;

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

    private void putWithoutCheck(String key, Object value) {
        super.put(key, value);
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

    /**
     * 删除值空的键值对.
     * @param deep 是否循环删除后代中的空值键值对.
     */
    public void removeEmptyValue(boolean deep) {
        super.entrySet().removeIf(entry -> entry.getValue() == null);
        if (deep) {
            super.forEach((k, v) -> {
                if (v instanceof ConfigurationHashMap) {
                    ((ConfigurationHashMap) v).removeEmptyValue(true);
                }
            });
        }

    }

    private void setParent(ConfigurationHashMap parent) {
        this.parent = parent;
    }

    /**
     * 获取上一节点.
     * @return
     */
    public ConfigurationHashMap getParent() {
        return parent;
    }

    public ConfigurationHashMap createMap(String key) {
        ConfigurationHashMap configurationHashMap = new ConfigurationHashMap();
        configurationHashMap.setParent(this);
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
        ConfigurationHashMap configurationHashMap = (ConfigurationHashMap) super.get(key);
        if (configurationHashMap != null) configurationHashMap.setParent(this);
        return configurationHashMap;
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

    public String toJson() {
        return GSON.toJson(this);
    }

    public static ConfigurationHashMap fromJson(String json) {
        return GSON.fromJson(json, ConfigurationHashMap.class);
    }
}
