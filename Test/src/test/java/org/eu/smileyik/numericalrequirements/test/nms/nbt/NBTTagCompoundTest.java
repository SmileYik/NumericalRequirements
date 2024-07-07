package org.eu.smileyik.numericalrequirements.test.nms.nbt;

import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@NeedTest
public class NBTTagCompoundTest {
    @NeedTest
    void getKeys() {
        NBTTagCompound tag = new NBTTagCompound();
        Set<String> keys = tag.getKeys();
        System.out.println("it should be empty" + keys);
        assert keys.isEmpty();
        tag.setString("key1", "value1");
        keys = tag.getKeys();
        System.out.println("it should be [key1]: " + keys);
        assert keys.contains("key1");
        tag.setString("key2", "value2");
        keys = tag.getKeys();
        System.out.println("it should be [key1, key2]: " + keys);
        assert keys.contains("key2") && keys.contains("key1");
    }

    @NeedTest
    void getTypeId() {
        assert NBTTagTypeId.COMPOUND == new NBTTagCompound().getTypeId() : "type id must be equal";
    }

    @NeedTest
    void size() {
        NBTTagCompound tag = new NBTTagCompound();
        assert tag.size() == 0 : "size must be zero";
        tag.setInt("int", 123);
        assert tag.size() == 1 : "size must be 1";
        tag.setLong("long", 123L);
        assert tag.size() == 2 : "size must be 2";
    }

    @NeedTest
    void set() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("int", 123);
        NBTTagCompound tag2 = new NBTTagCompound();
        tag2.set("tag", tag);
        System.out.println("tag: " + tag);
        System.out.println("tag2: " + tag2);
    }

    @NeedTest
    void setByte() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
    }

    @NeedTest
    void setShort() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setShort("Short", (short) 1);
        System.out.println(tag);
    }

    @NeedTest
    void setInt() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("Int", 1);
        System.out.println(tag);
    }

    @NeedTest
    void setLong() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("Long", 1L);
        System.out.println(tag);
    }

    @NeedTest
    void setUUID() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUUID("uuid", UUID.randomUUID());
        System.out.println(tag);
    }

    @NeedTest
    void getUUID() {
        NBTTagCompound tag = new NBTTagCompound();
        UUID uuid = UUID.randomUUID();
        tag.setUUID("uuid", uuid);
        UUID get = tag.getUUID("uuid");
        assert uuid.equals(get) : "UUID " + get + " not equal to " + get;
    }

    @NeedTest
    void isUUID() {
        NBTTagCompound tag = new NBTTagCompound();
        UUID uuid = UUID.randomUUID();
        tag.setUUID("uuid", uuid);
        tag.setString("string", "string");
        boolean isUUID = tag.isUUID("uuid");
        assert isUUID : "key 'uuid' must be uuid";
        isUUID = tag.isUUID("string");
        assert !isUUID : "key 'string' is not uuid";
    }

    @NeedTest
    void setFloat() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("Float", 1.0f);
        System.out.println(tag);
    }

    @NeedTest
    void setDouble() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("Double", 1.0);
        System.out.println(tag);
    }

    @NeedTest
    void setString() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("String", "string");
        System.out.println(tag);
    }

    @NeedTest
    void setByteArray() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("ByteArray", new byte[] {1, 2, 3});
        System.out.println(tag);
    }

    @NeedTest
    void setBoolean() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Boolean", true);
        System.out.println(tag);
    }

    @NeedTest
    void get() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        Object o = tag.get("Byte");
        assert o != null : "key 'Byte' not be null";
        System.out.println(o.getClass());
    }

    @NeedTest
    void testGetTypeId() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        byte aByte = tag.getTypeId("Byte");
        assert aByte == NBTTagTypeId.BYTE : "key 'Byte' must be a byte";
    }

    @NeedTest
    void hasKey() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        assert tag.hasKey("Byte") : "it has key 'Byte'";
    }

    @NeedTest
    void hasKeyOfType() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        assert tag.hasKeyOfType("Byte", NBTTagTypeId.BYTE) : "key 'Byte' must be a byte";
    }

    @NeedTest
    void getByte() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        byte aByte = tag.getByte("Byte");
        assert aByte == (byte) 1 : "it's not equal to 1";
    }

    @NeedTest
    void getShort() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setShort("Short", (short) 1);
        System.out.println(tag);
        short aShort = tag.getShort("Short");
        assert aShort == (short) 1 : "it's not equal to 1";
    }

    @NeedTest
    void getInt() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("Int", 1);
        System.out.println(tag);
        int aInt = tag.getInt("Int");
        assert aInt == 1 : "it's not equal to 1";
    }

    @NeedTest
    void getLong() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("Long", 1L);
        System.out.println(tag);
        long aLong = tag.getLong("Long");
        assert aLong == 1L : "it's not equal to 1";
    }

    @NeedTest
    void getFloat() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("Float", 1.0f);
        System.out.println(tag);
        float aFloat = tag.getFloat("Float");
        assert aFloat == 1.0f : "it's not equal to 1";
    }

    @NeedTest
    void getDouble() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("Double", 1.0);
        System.out.println(tag);
        double aDouble = tag.getDouble("Double");
        assert aDouble == 1.0 : "it's not equal to 1";
    }

    @NeedTest
    void getString() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("String", "string");
        System.out.println(tag);
        String aString = tag.getString("String");
        assert aString.equals("string") : "it's not equal to 'string'";
    }

    @NeedTest
    void getByteArray() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("ByteArray", new byte[] {1, 2, 3});
        System.out.println(tag);
        byte[] aByteArray = tag.getByteArray("ByteArray");
        assert Arrays.equals(aByteArray, new byte[] {1, 2, 3}) : "not equal";
    }

    @NeedTest
    void getIntArray() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray("IntArray", new int[] {1, 2, 3});
        System.out.println(tag);
        int[] aIntArray = tag.getIntArray("IntArray");
        assert Arrays.equals(aIntArray, new int[] {1, 2, 3}) : "not equal";
    }

    @NeedTest
    void getCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound tag2 = new NBTTagCompound();
        tag2.setByte("Byte", (byte) 1);
        System.out.println("tag2: " + tag2);
        tag.set("tag", tag2);
        System.out.println("tag: " + tag);
        NBTTagCompound tag3 = tag.getCompound("tag");
        System.out.println("tag3: " + tag3);
        byte aByte = tag3.getByte("Byte");
        assert aByte == (byte) 1 : "it's not equal to 1";
    }

//    @NeedTest
//    void getList() {
//    }

    @NeedTest
    void getBoolean() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Boolean", true);
        System.out.println(tag);
        boolean aBoolean = tag.getBoolean("Boolean");
        assert aBoolean : "it's not equal to true";
    }

    @NeedTest
    void remove() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Byte", (byte) 1);
        System.out.println(tag);
        tag.remove("Byte");
        System.out.println(tag);
        assert !tag.hasKey("Byte");
    }

    @NeedTest
    void isEmpty() {
        NBTTagCompound tag = new NBTTagCompound();
        assert tag.isEmpty() : "it must be empty";
        tag.setByte("Byte", (byte) 1);
        assert !tag.isEmpty() : "it must not be empty";
    }

//    @NeedTest
//    void merge() {
//    }
}
