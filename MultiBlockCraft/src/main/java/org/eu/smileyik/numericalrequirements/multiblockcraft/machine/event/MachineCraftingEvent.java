package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.event.Cancellable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;

/**
 * 机器运作中事件。当机器正在制作配方物品时触发。
 * 当取消该事件时，将会终止制作当前正在制作的配方，并且不会进入产生产物环节。
 */
public class MachineCraftingEvent extends MachineDataEvent implements Cancellable {
    private boolean cancelled;

    public MachineCraftingEvent(Machine machine, String identifier, MachineDataUpdatable machineData) {
        super(machine, identifier, machineData);
    }

    public MachineCraftingEvent(boolean isAsync, Machine machine, String identifier, MachineDataUpdatable machineData) {
        super(isAsync, machine, identifier, machineData);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public MachineDataUpdatable getMachineData() {
        return (MachineDataUpdatable) super.getMachineData();
    }
}
