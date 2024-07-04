package org.eu.smileyik.numericalrequirements.test.nbtItem;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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
        cast.put("string", "string");
        cast.put("int", (int) 32);
        cast.put("long", (long) 64);
        cast.put("byte", (byte) 8);
        cast.put("short", (short) 16);
        cast.put("boolean", true);
        cast.put("float", 3.14f);
        cast.put("double", 3.141592653589793d);
        cast.put("intArray", new int[] { 1, 2, 3 });
        cast.put("byteArray", new byte[] { 4, 5, 6 });
        cast.put("uuid", UUID.randomUUID());
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
        ItemStack copy = cast.append("string", "string")
                            .append("int", (int) 32)
                            .append("long", (long) 64)
                            .append("byte", (byte) 8)
                            .append("short", (short) 16)
                            .append("boolean", true)
                            .append("float", 3.14f)
                            .append("double", 3.141592653589793d)
                            .append("intArray", new int[] { 1, 2, 3 })
                            .append("byteArray", new byte[] { 4, 5, 6 })
                            .append("uuid", UUID.randomUUID())
                            .getItemStack();
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
        System.out.println("Key 'string': " + cast.containsKey("string"));
        System.out.println("Key 'int': " + cast.containsKey("int"));
        System.out.println("Key 'byte': " + cast.containsKey("byte"));
        System.out.println("Key 'short': " + cast.containsKey("short"));
        System.out.println("Key 'long': " + cast.containsKey("long"));
        System.out.println("Key 'intArray': " + cast.containsKey("intArray"));
        System.out.println("Key 'byteArray': " + cast.containsKey("byteArray"));
        System.out.println("Key 'boolean': " + cast.containsKey("boolean"));
        System.out.println("Key 'float': " + cast.containsKey("float"));
        System.out.println("Key 'double': " + cast.containsKey("double"));
        System.out.println("Key 'uuid': " + cast.containsKey("uuid"));
    }

    @NeedTest
    public void getNbtTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";

        System.out.println("Key 'string': " + cast.getString("string"));
        System.out.println("Key 'int': " + cast.getInt("int"));
        System.out.println("Key 'byte': " + cast.getByte("byte"));
        System.out.println("Key 'short': " + cast.getShort("short"));
        System.out.println("Key 'long': " + cast.getLong("long"));
        System.out.println("Key 'intArray': " + Arrays.toString(cast.getIntArray("intArray")));
        System.out.println("Key 'byteArray': " + Arrays.toString(cast.getByteArray("byteArray")));
        System.out.println("Key 'boolean': " + cast.getBoolean("boolean"));
        System.out.println("Key 'float': " + cast.containsKey("float"));
        System.out.println("Key 'double': " + cast.getDouble("double"));
        System.out.println("Key 'uuid': " + cast.getUUID("uuid"));
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
        System.out.println("Key 'uuid': " + Objects.toString(cast.getUUID("uuid")));
        cast.put("uuid", null);
        System.out.println("Has key 'uuid': " + cast.containsKey("uuid"));
        assert !cast.containsKey("uuid") : "Key 'uuid' does not be delete";
    }

    @NeedTest
    public void removeAndGetNullTest() {
        ItemStack item = putNBTTest();
        NBTItem cast = NBTItemHelper.cast(item);
        assert cast != null : "NBT Item cast returned null";
        System.out.println("Key 'uuid': " + Objects.toString(cast.getUUID("uuid")));
        cast.remove("uuid");
        System.out.println("Has key 'uuid': " + cast.containsKey("uuid"));
        assert !cast.containsKey("uuid") : "Key 'uuid' does not be delete.";
    }

    private String saveItemToString(String key, ItemStack itemStack) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set(key, itemStack);
        return yaml.saveToString();
    }
}
