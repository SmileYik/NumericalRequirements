package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

/**
 * 机器数据移除事件，当机器数据被移除时触发（例如机器主方块被拆）。
 */
public class MachineDataRemoveEvent extends MachineDataEvent {
    public MachineDataRemoveEvent(Machine machine, String identifier, MachineData machineData) {
        super(machine, identifier, machineData);
    }

    public MachineDataRemoveEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData) {
        super(isAsync, machine, identifier, machineData);
    }
}
