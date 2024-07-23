package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;

public class MachineStatusButton extends ItemButton {
    @Override
    protected boolean getStatus(ItemStack item, MachineData data) {
        return data instanceof MachineDataUpdatable &&  ((MachineDataUpdatable) data).isEnable();
    }

    @Override
    protected void setStatus(ItemStack item, MachineData data, boolean status) {
        if (data instanceof MachineDataUpdatable) {
            ((MachineDataUpdatable) data).setEnable(status);
        }
    }
}
