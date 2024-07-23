package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.util.ItemUtil;

import java.text.MessageFormat;

public class ProcessBar implements InvItem {
    private String name;
    private String disableName;
    private int start, end;

    @Override
    public ItemStack update(ItemStack item, MachineData data) {
        if (!(data instanceof MachineDataUpdatable)) {
            return item;
        }
        MachineDataUpdatable d = (MachineDataUpdatable) data;
        double totalTime = d.getTotalTime();
        double rate = totalTime == 0 ? 0 : d.getCraftedTime() / totalTime;
        int i = (int) Math.round(rate * (end - start) + start);
        ItemStack itemS = ItemUtil.setCustomModelData(item, i);
        ItemMeta meta = itemS.getItemMeta();
        if (rate == 0) {
            meta.setDisplayName(disableName);
        } else {
            meta.setDisplayName(MessageFormat.format(name, String.format("%.0f", rate * 100)));
        }
        itemS.setItemMeta(meta);
        return itemS;
    }

    @Override
    public void load(ConfigurationSection section) {
        disableName = ChatColor.translateAlternateColorCodes('&', section.getString("disable-name"));
        name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
        start = section.getInt("start");
        end = section.getInt("end");
    }
}
