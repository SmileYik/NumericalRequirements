package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

public class NBTSyncEntry implements YamlItemEntry {
    final boolean flag;

    public NBTSyncEntry() {
        boolean flag0 = true;

        ItemStack apple = new ItemStack(Material.APPLE);
        try {
            NBTItem item = NBTItemHelper.cast(apple);
            if (item == null) {
                flag0 = false;
            }
        } catch (Exception e) {
            DebugLogger.debug(e);
            flag0 = false;
        }
        flag = flag0;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getId() {
        return "nbt-sync";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        NBTItem item = NBTItemHelper.cast(itemStack);
        if (item == null) return;
        NBTTagCompound tag = item.getTag();
        if (tag == null) return;
        if (tag.hasKeyOfType(ItemService.NBT_KEY_SYNC, NBTTagTypeId.BYTE)) {
            section.set(getId(), tag.getBoolean(ItemService.NBT_KEY_SYNC));
        }
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!section.contains(getId())) return null;
        NBTItem item = NBTItemHelper.cast(itemStack);
        if (item == null) return null;
        NBTTagCompound tag = item.getTag();
        if (tag == null) return null;
        tag.setBoolean(ItemService.NBT_KEY_SYNC, section.getBoolean(getId()));
        return item.getItemStack();
    }
}
