package org.eu.smileyik.numericalrequirements.test.nms.nbtItem;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@NeedTest
public class NbtItemTest {
    @NeedTest
    public void itemCastTest() throws ClassNotFoundException {
        ItemStack item = new ItemStack(Material.APPLE);
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        System.out.println("NBTItem: " + cast.getClass());
    }

    @NeedTest
    public ItemStack putNBTTest() {
        ItemStack item = new ItemStack(Material.APPLE);
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";
        tag.put("string", "string");
        tag.put("int", (int) 32);
        tag.put("long", (long) 64);
        tag.put("byte", (byte) 8);
        tag.put("short", (short) 16);
        tag.put("boolean", true);
        tag.put("float", 3.14f);
        tag.put("double", 3.141592653589793d);
        tag.put("intArray", new int[] { 1, 2, 3 });
        tag.put("byteArray", new byte[] { 4, 5, 6 });
        tag.put("uuid", UUID.randomUUID());
        ItemStack copy = cast.getItemStack();
        assert copy != null : "NBT Item copy returned null";
        System.out.println("item == copy: " + (copy == item));
        System.out.println(saveItemToString("item", item));
        System.out.println(saveItemToString("nbt-item", copy));
        return copy;
    }

    @NeedTest
    public ItemStack appendNBTTest() {
        ItemStack item = new ItemStack(Material.APPLE);
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";
        tag.append("string", "string")
                .append("int", (int) 32)
                .append("long", (long) 64)
                .append("byte", (byte) 8)
                .append("short", (short) 16)
                .append("boolean", true)
                .append("float", 3.14f)
                .append("double", 3.141592653589793d)
                .append("intArray", new int[] { 1, 2, 3 })
                .append("byteArray", new byte[] { 4, 5, 6 })
                .append("uuid", UUID.randomUUID());
        ItemStack copy = cast.getItemStack();
        assert copy != null : "NBT Item copy returned null";
        System.out.println("item == copy: " + (copy == item));
        System.out.println(saveItemToString("item", item));
        System.out.println(saveItemToString("nbt-item", copy));
        return copy;
    }

    @NeedTest
    public void containsNBTKeyTest() {
        ItemStack item = appendNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";

        System.out.println("Key 'string': " + tag.hasKey("string"));
        System.out.println("Key 'int': " + tag.hasKey("int"));
        System.out.println("Key 'byte': " + tag.hasKey("byte"));
        System.out.println("Key 'short': " + tag.hasKey("short"));
        System.out.println("Key 'long': " + tag.hasKey("long"));
        System.out.println("Key 'intArray': " + tag.hasKey("intArray"));
        System.out.println("Key 'byteArray': " + tag.hasKey("byteArray"));
        System.out.println("Key 'boolean': " + tag.hasKey("boolean"));
        System.out.println("Key 'float': " + tag.hasKey("float"));
        System.out.println("Key 'double': " + tag.hasKey("double"));
        System.out.println("Key 'uuid': " + tag.hasKey("uuid"));
    }

    @NeedTest
    public void getNbtTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";

        System.out.println("Key 'string': " + tag.getString("string"));
        System.out.println("Key 'int': " + tag.getInt("int"));
        System.out.println("Key 'byte': " + tag.getByte("byte"));
        System.out.println("Key 'short': " + tag.getShort("short"));
        System.out.println("Key 'long': " + tag.getLong("long"));
        System.out.println("Key 'intArray': " + Arrays.toString(tag.getIntArray("intArray")));
        System.out.println("Key 'byteArray': " + Arrays.toString(tag.getByteArray("byteArray")));
        System.out.println("Key 'boolean': " + tag.getBoolean("boolean"));
        System.out.println("Key 'float': " + tag.hasKey("float"));
        System.out.println("Key 'double': " + tag.getDouble("double"));
        System.out.println("Key 'uuid': " + tag.getUUID("uuid"));
    }

    @NeedTest
    public void saveToStringTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        System.out.println(cast.saveToString());
    }

    @NeedTest
    public void putAndGetNullTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";
        System.out.println("Key 'uuid': " + Objects.toString(tag.getUUID("uuid")));
        tag.put("uuid", null);
        System.out.println("Has key 'uuid': " + tag.hasKey("uuid"));
        assert !tag.hasKey("uuid") : "Key 'uuid' does not be delete";
    }

    @NeedTest
    public void removeAndGetNullTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        NBTTagCompound tag = cast.getTag();
        assert tag != null : "getTag returned null";
        System.out.println("Key 'uuid': " + Objects.toString(tag.getUUID("uuid")));
        tag.remove("uuid");
        System.out.println("Has key 'uuid': " + tag.hasKey("uuid"));
        assert !tag.hasKey("uuid") : "Key 'uuid' does not be delete.";
    }

    private String saveItemToString(String key, ItemStack itemStack) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set(key, itemStack);
        return yaml.saveToString();
    }
}
