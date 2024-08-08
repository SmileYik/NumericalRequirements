package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.multiblock;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleMultiBlockMachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.ItemButton;

import java.text.MessageFormat;

public class ChangeDirectionButton extends ItemButton {
    @Override
    protected boolean getStatus(ItemStack item, MachineData data) {
        return data instanceof SimpleMultiBlockMachineData && ((SimpleMultiBlockMachineData) data).isValid();
    }

    @Override
    protected void setStatus(ItemStack item, MachineData data, boolean status) {
        if (data instanceof  SimpleMultiBlockMachineData) {
            ((SimpleMultiBlockMachineData) data).nextFace();
        }
    }

    @Override
    public ItemStack update(ItemStack item, MachineData data) {
        if (data instanceof  SimpleMultiBlockMachineData) {
            item = super.update(item, data);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(MessageFormat.format(itemMeta.getDisplayName(), ((SimpleMultiBlockMachineData) data).getFace().name()));
            item.setItemMeta(itemMeta);
            return item;
        }
        return super.update(item, data);
    }
}
