package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

/**
 * 创建机器数据事件。此事件将在玩家第一次打开某个新机器时触发。
 */
public class CreateMachineDataEvent extends MachineDataEvent {

    public CreateMachineDataEvent(Machine machine, String identifier, MachineData machineData) {
        super(machine, identifier, machineData);
    }

    public CreateMachineDataEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData) {
        super(isAsync, machine, identifier, machineData);
    }
}
