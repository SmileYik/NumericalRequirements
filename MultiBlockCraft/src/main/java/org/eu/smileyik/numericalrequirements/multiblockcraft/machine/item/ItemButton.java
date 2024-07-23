package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.util.ItemUtil;

public abstract class ItemButton implements InvItem {
    private String enabledName;
    private String disabledName;
    private int enabledModelId;
    private int disabledModelId;

    protected abstract boolean getStatus(ItemStack item, MachineData data);

    protected abstract void setStatus(ItemStack item, MachineData data, boolean status);

    public ItemStack click(ItemStack item, MachineData data) {
        setStatus(item, data, !getStatus(item, data));
        return update(item, data);
    }

    @Override
    public ItemStack update(ItemStack item, MachineData data) {
        boolean flag = getStatus(item, data);
        String name;
        int id;
        if (flag) {
            name = enabledName;
            id = enabledModelId;
        } else {
            name = disabledName;
            id = disabledModelId;
        }

        ItemStack copy = ItemUtil.setCustomModelData(item, id);
        ItemMeta itemMeta = copy.getItemMeta();
        itemMeta.setDisplayName(name);
        copy.setItemMeta(itemMeta);
        return copy;
    }

    @Override
    public void load(ConfigurationSection section) {
        enabledName = ChatColor.translateAlternateColorCodes('&', section.getString("enabled.name"));
        disabledName = ChatColor.translateAlternateColorCodes('&', section.getString("disabled.name"));
        enabledModelId = section.getInt("enabled.model-id");
        disabledModelId = section.getInt("disabled.model-id");
    }
}
