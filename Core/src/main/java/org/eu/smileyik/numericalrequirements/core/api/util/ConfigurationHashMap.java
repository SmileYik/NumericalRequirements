package org.eu.smileyik.numericalrequirements.core.api.util;

import com.google.gson.*;
import org.bukkit.ChatColor;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConfigurationHashMap extends HashMap<String, Object> {
    public static final Object OBJECT_DESERIALIZER = (JsonDeserializer<ConfigurationHashMap>) (json, typeOfT, context) -> {
        if (json instanceof JsonObject) {
            ConfigurationHashMap map = new ConfigurationHashMap();
            for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                JsonElement value = entry.getValue();
                Class<?> clazz = value instanceof JsonObject ? ConfigurationHashMap.class : (value instanceof JsonArray ? List.class : Object.class);
                map.putWithoutCheck(entry.getKey(), context.deserialize(value, clazz));
            }
            return map;
        }
        if (json instanceof JsonArray) {
            return context.deserialize(json, List.class);
        }
        return context.deserialize(json, Object.class);
    };
    public static final Object LIST_DESERIALIZER = (JsonDeserializer<List<?>>) (json, typeOfT, context) -> {
        if (json instanceof JsonArray) {
            JsonArray array = json.getAsJsonArray();
            if (array.size() > 0 && array.get(0) instanceof JsonObject) {
                List<ConfigurationHashMap> list = new LinkedList<>();
                for (JsonElement value : array) {
                    list.add(context.deserialize(value, ConfigurationHashMap.class));
                }
                return list;
            } else {
                return context.deserialize(json, Object.class);
            }
        }
        return context.deserialize(json, Object.class);
    };


    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ConfigurationHashMap.class, OBJECT_DESERIALIZER)
            .registerTypeAdapter(List.class, LIST_DESERIALIZER)
            .create();

    public static final Gson GSON_PRETTY = new GsonBuilder()
            .registerTypeAdapter(ConfigurationHashMap.class, OBJECT_DESERIALIZER)
            .registerTypeAdapter(List.class, LIST_DESERIALIZER)
            .setPrettyPrinting()
            .create();

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

    public List<ConfigurationHashMap> getMapList(String key) {
        return getList(key, ConfigurationHashMap.class);
    }

    public <T extends ConfigurationHashMapSerializable> T get(String key, Class<T> clazz) {
        if (!isMap(key)) return null;
        T t = null;
        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            DebugLogger.debug(e);
            return null;
        }
        t.fromConfigurationHashMap(getMap(key));
        return t;
    }

    public <T extends ConfigurationHashMapSerializable> List<T> getValueList(String key, Class<T> clazz) {
        List<ConfigurationHashMap> mapList = getMapList(key);
        List<T> result = new ArrayList<>();
        for (ConfigurationHashMap map : mapList) {
            try {
                T t = clazz.getDeclaredConstructor().newInstance();
                t.fromConfigurationHashMap(map);
                result.add(t);
            } catch (Throwable e) {
                DebugLogger.debug(e);
                continue;
            }
        }
        return result;
    }

    public String toJson() {
        return toJson(false);
    }

    public String toJson(boolean pretty) {
        return (pretty ? GSON_PRETTY : GSON).toJson(this);
    }

    public static ConfigurationHashMap fromJson(String json) {
        return GSON.fromJson(json, ConfigurationHashMap.class);
    }

    public static class Builder {
        private final ConfigurationHashMap map = new ConfigurationHashMap();

        public Builder put(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public ConfigurationHashMap build() {
            return map;
        }

        public static ConfigurationHashMap asMap(Object ...obj) {
            ConfigurationHashMap map = new ConfigurationHashMap();
            int length = obj.length;
            for (int i = 0; i < length; i += 2) {
                map.put(obj[i].toString(), obj[i + 1]);
            }
            return map;
        }
    }
}
