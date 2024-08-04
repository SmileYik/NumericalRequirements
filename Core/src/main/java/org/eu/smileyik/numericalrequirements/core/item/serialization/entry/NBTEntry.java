package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.*;

public class NBTEntry implements ItemSerializerEntry {
    final boolean flag;
    private Map<String, NBTTypeCast> castMap;
    private Map<Byte, NBTTypeSerialize> serializeMap;
    private Set<String> ignoreKeys;

    public NBTEntry() {
        boolean flag0 = true;

        ItemStack apple = new ItemStack(Material.APPLE);
        try {
            NBTItem item = NBTItemHelper.cast(apple);
            if (item == null) {
                flag0 = false;
            }

            castMap = new HashMap<>();
            castMap.put("S", s -> s);
            castMap.put("i", Integer::parseInt);
            castMap.put("b", Byte::parseByte);
            castMap.put("s", Short::parseShort);
            castMap.put("l", Long::parseLong);
            castMap.put("f", Float::parseFloat);
            castMap.put("d", Double::parseDouble);
            castMap.put("u", UUID::fromString);
//            castMap.put("B", Boolean::parseBoolean);
            castMap.put("ia", s -> {
                String[] nums = s.substring(1, s.length() - 1).replace(" ", "").split(";");
                int[] arrays = new int[nums.length];
                for (int i = 0; i < nums.length; i++) {
                    arrays[i] = Integer.parseInt(nums[i]);
                }
                return arrays;
            });
            castMap.put("ba", s -> {
                String[] nums = s.substring(1, s.length() - 1).replace(" ", "").split(";");
                byte[] arrays = new byte[nums.length];
                for (int i = 0; i < nums.length; i++) {
                    arrays[i] = Byte.parseByte(nums[i]);
                }
                return arrays;
            });

            serializeMap = new HashMap<>();
            serializeMap.put(NBTTagTypeId.STRING, (tag, key) -> String.format("%sS", tag.getString(key)));
            serializeMap.put(NBTTagTypeId.INT, (tag, key) -> String.format("%di", tag.getInt(key)));
            serializeMap.put(NBTTagTypeId.BYTE, (tag, key) -> String.format("%db", tag.getByte(key)));
            serializeMap.put(NBTTagTypeId.SHORT, (tag, key) -> String.format("%ds", tag.getShort(key)));
            serializeMap.put(NBTTagTypeId.LONG, (tag, key) -> String.format("%dl", tag.getLong(key)));
            serializeMap.put(NBTTagTypeId.FLOAT, (tag, key) -> String.format("%ff", tag.getFloat(key)));
            serializeMap.put(NBTTagTypeId.DOUBLE, (tag, key) -> String.format("%fd", tag.getDouble(key)));
            // serializeMap.put(NBTTagTypeId.UUID, (tag, key) -> String.format("%su", tag.getUUID(key).toString()));
            // serializeMap.put(NBTTagTypeId.BOOLEAN, (tag, key) -> String.format("%db", tag.getByte(key)));
            serializeMap.put(NBTTagTypeId.INT_ARRAY, (tag, key) -> {
                if (NBTTagCompound.hasMethod("isUUID") && tag.isUUID(key)) {
                    return String.format("%su", tag.getUUID(key).toString());
                }
                return String.format("%sia", Arrays.toString(tag.getIntArray(key)));
            });
            serializeMap.put(NBTTagTypeId.BYTE_ARRAY, (tag, key) -> String.format("%sba", Arrays.toString(tag.getByteArray(key))));
        } catch (Exception e) {
            DebugLogger.debug(e);
            flag0 = false;
        }
        flag = flag0;
    }

    @Override
    public void configure(ConfigurationSection section) {
        ignoreKeys = new HashSet<>(section.getStringList("ignore-keys"));
        ignoreKeys.add(ItemService.NBT_KEY_ID);
    }

    @Override
    public String getId() {
        return "nbt";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        NBTItem nbtItem = NBTItemHelper.cast(itemStack);
        if (nbtItem == null) return;
        NBTTagCompound tag = nbtItem.getTag();
        if (tag == null || tag.isEmpty()) return;
        serialize(tag, section);
    }

    private void serialize(NBTTagCompound tag, ConfigurationHashMap section) {
        for (String key : tag.getKeys()) {
            if (ignoreKeys.contains(key)) continue;
            if (tag.hasKeyOfType(key, NBTTagTypeId.COMPOUND)) {
                serialize(tag.getCompound(key), section.createMap(key));
                continue;
            }
            serializeMap.forEach((type, cast) -> {
                if (tag.hasKeyOfType(key, type)) {
                    section.put(key, cast.cast(tag, key));
                }
            });
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        NBTItem nbtItem = NBTItemHelper.cast(itemStack);
        if (nbtItem == null) return null;

        NBTTagCompound tag = nbtItem.getTag();
        deserialize(tag, section);
        nbtItem.setTag(tag);

        return nbtItem.getItemStack();
    }

    private void deserialize(NBTTagCompound tag, ConfigurationHashMap section) {
        Set<String> keys = section.keySet();
        for (String key : keys) {
            if (ignoreKeys.contains(key)) continue;
            if (section.isMap(key)) {
                NBTTagCompound subTag = new NBTTagCompound();
                deserialize(subTag, section.getMap(key));
                tag.set(key, subTag);
                continue;
            }
            String str = section.getString(key);
            if (str == null || str.isEmpty()) continue;
            castMap.forEach((type, cast) -> {
                if (str.endsWith(type)) {
                    String value = str.substring(0, str.length() - type.length());
                    Object obj = cast.cast(value);
                    tag.put(key, obj);
                }
            });
        }
    }

    private interface NBTTypeCast {
        Object cast(String str);
    }

    private interface NBTTypeSerialize {
        String cast(NBTTagCompound tag, String key);
    }
}
