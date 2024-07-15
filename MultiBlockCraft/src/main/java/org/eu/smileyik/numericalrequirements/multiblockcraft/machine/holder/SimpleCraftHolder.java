package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder;

import org.bukkit.inventory.Inventory;
import org.eu.smileyik.numericalrequirements.multiblockcraft.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public class SimpleCraftHolder implements CraftHolder {
    private Machine machine;
    private String identifier;
    private Inventory inventory;
    private MachineData machineData;

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

    public void setMachineData(MachineData machineData) {
        this.machineData = machineData;
    }

    @Override
    public MachineData getMachineData() {
        return machineData;
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
