package org.eu.smileyik.numericalrequirements.nms.nbt;

import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class NBTTagCompound {
    private static final ReflectClass CLAZZ;
    private static final Class<?> NBTBASE_CLASS;

    static {
        ReflectClassBuilder currentVersion = NBTTagCompoundHelper.getCurrentVersion();
        if (currentVersion != null) {
            try {
                CLAZZ = currentVersion.toClassForce();
            } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            CLAZZ = null;
        }
        if (CLAZZ == null) {
            NBTBASE_CLASS = null;
        } else {
            NBTBASE_CLASS = CLAZZ.getMethod("set").getMethod().getParameters()[1].getType();
        }
    }

    private final Object instance;

    public NBTTagCompound() {
        if (CLAZZ == null) {
            throw new NullPointerException("NBTTagCompound class is null");
        }
        instance = CLAZZ.newInstance("new");
    }

    public NBTTagCompound(Object instance) {
        if (CLAZZ == null) {
            throw new NullPointerException("NBTTagCompound class is null");
        }
        this.instance = instance;
    }

    public Set<String> getKeys() {
        return (Set<String>) CLAZZ.execute("getKeys", instance);
    }

    public byte getTypeId() {
        return (byte) CLAZZ.execute("getTypeId", instance);
    }

    public int size() {
        if (CLAZZ.hasMethod("size"))
            return (int) CLAZZ.execute("size", instance);
        return 0;
    }

    public void set(String key, Object value) {
        CLAZZ.execute("set", instance, key, value);
    }

    public void setByte(String key, byte value) {
        CLAZZ.execute("setByte", instance, key, value);
    }

    public void setShort(String key, short value) {
        CLAZZ.execute("setShort", instance, key, value);
    }

    public void setInt(String key, int value) {
        CLAZZ.execute("setInt", instance, key, value);
    }

    public void setLong(String key, long value) {
        CLAZZ.execute("setLong", instance, key, value);
    }

    public void setUUID(String key, UUID value) {
        CLAZZ.execute("setUUID", instance, key, value);
    }

    public UUID getUUID(String key) {
        if (CLAZZ.hasMethod("getUUID"))
            return (UUID) CLAZZ.execute("getUUID", instance, key);
        return UUID.fromString(getString(key));
    }

    public boolean isUUID(String key) {
        return (boolean) CLAZZ.execute("isUUID", instance, key);
    }

    public void setFloat(String key, float value) {
        CLAZZ.execute("setFloat", instance, key, value);
    }

    public void setDouble(String key, double value) {
        CLAZZ.execute("setDouble", instance, key, value);
    }

    public void setString(String key, String value) {
        CLAZZ.execute("setString", instance, key, value);
    }

    public void setByteArray(String key, byte[] value) {
        CLAZZ.execute("setByteArray", instance, key, value);
    }

    public void setBoolean(String key, boolean value) {
        CLAZZ.execute("setBoolean", instance, key, value);
    }

    public Object get(String key) {
        return CLAZZ.execute("get", instance, key);
    }

    public byte getTypeId(String key) {
        return (byte) CLAZZ.execute("getKeyTypeId", instance, key);
    }

    public boolean hasKey(String key) {
        return (boolean) CLAZZ.execute("hasKey", instance, key);
    }

    public boolean hasKeyOfType(String key, int type) {
        return (boolean) CLAZZ.execute("hasKeyOfType", instance, key, type);
    }

    public byte getByte(String key) {
        return (byte) CLAZZ.execute("getByte", instance, key);
    }

    public short getShort(String key) {
        return (short) CLAZZ.execute("getShort", instance, key);
    }

    public int getInt(String key) {
        return (int) CLAZZ.execute("getInt", instance, key);
    }

    public long getLong(String key) {
        return (long) CLAZZ.execute("getLong", instance, key);
    }

    public float getFloat(String key) {
        return (float) CLAZZ.execute("getFloat", instance, key);
    }

    public double getDouble(String key) {
        return (double) CLAZZ.execute("getDouble", instance, key);
    }

    public String getString(String key) {
        return (String) CLAZZ.execute("getString", instance, key);
    }

    public byte[] getByteArray(String key) {
        return (byte[]) CLAZZ.execute("getByteArray", instance, key);
    }

    public int[] getIntArray(String key) {
        return (int[]) CLAZZ.execute("getIntArray", instance, key);
    }

    public NBTTagCompound getCompound(String key) {
        Object execute = CLAZZ.execute("getCompound", instance, key);
        if (execute == null) return null;
        return new NBTTagCompound(execute);
    }

    public Object getList(String key, int var) {
        return CLAZZ.execute("getList", instance, key, var);
    }

    public boolean getBoolean(String key) {
        return (boolean) CLAZZ.execute("getBoolean", instance, key);
    }

    public void remove(String key) {
        CLAZZ.execute("remove", instance, key);
    }

    public boolean isEmpty() {
        return (boolean) CLAZZ.execute("isEmpty", instance);
    }

    public boolean merge(NBTTagCompound other) {
        return (boolean) CLAZZ.execute("merge", instance, other.instance);
    }

    public NBTTagCompound append(String key, Object value) {
        put(key, value);
        return this;
    }

    public void put(String key, Object value) {
        if (value == null) {
            remove(key);
        } else if (value instanceof UUID) {
            if (CLAZZ.hasMethod("setUUID")) {
                CLAZZ.execute("setUUID", instance, key, value);
            } else {
                CLAZZ.execute("setString", instance, key, Objects.toString(value));
            }
        } else if (value instanceof String) {
            CLAZZ.execute("setString", instance, key, value);
        } else if (value instanceof Integer) {
            CLAZZ.execute("setInt", instance, key, value);
        } else if (value instanceof Double) {
            CLAZZ.execute("setDouble", instance, key, value);
        } else if (value instanceof Boolean) {
            CLAZZ.execute("setBoolean", instance, key, value);
        } else if (value instanceof Float) {
            CLAZZ.execute("setFloat", instance, key, value);
        } else if (value instanceof Long) {
            CLAZZ.execute("setLong", instance, key, value);
        } else if (value instanceof Short) {
            CLAZZ.execute("setShort", instance, key, value);
        } else if (value instanceof Byte) {
            CLAZZ.execute("setByte", instance, key, value);
        } else if (value instanceof int[]) {
            CLAZZ.execute("setIntArray", instance, key, value);
        } else if (value instanceof byte[]) {
            CLAZZ.execute("setByteArray", instance, key, value);
        } else if (NBTBASE_CLASS.isAssignableFrom(value.getClass())) {
            CLAZZ.execute("set", instance, key, value);
        }
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return Objects.toString(instance);
    }
}
