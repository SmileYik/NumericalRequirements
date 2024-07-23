package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.multiblock;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleMultiBlockMachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.ItemButton;

public class StructureStatus extends ItemButton {
    @Override
    protected boolean getStatus(ItemStack item, MachineData data) {
        return data instanceof SimpleMultiBlockMachineData && ((SimpleMultiBlockMachineData) data).isValid();
    }

    @Override
    protected void setStatus(ItemStack item, MachineData data, boolean status) {

    }
}
