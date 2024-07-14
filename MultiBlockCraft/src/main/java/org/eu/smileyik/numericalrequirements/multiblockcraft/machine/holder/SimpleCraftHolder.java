package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder;

import org.bukkit.inventory.Inventory;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public class SimpleCraftHolder implements CraftHolder {
    private Machine machine;
    private String identifier;
    private Inventory inventory;

    public SimpleCraftHolder() {

    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
