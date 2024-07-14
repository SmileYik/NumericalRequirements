package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder;

import org.bukkit.inventory.InventoryHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public interface CraftHolder extends InventoryHolder {
    Machine getMachine();
    String getIdentifier();
}
