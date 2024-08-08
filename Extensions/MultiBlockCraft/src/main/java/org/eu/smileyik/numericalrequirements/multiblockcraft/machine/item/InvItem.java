package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

public interface InvItem {

    ItemStack update(ItemStack item, MachineData data);

    void load(ConfigurationSection section);
}
