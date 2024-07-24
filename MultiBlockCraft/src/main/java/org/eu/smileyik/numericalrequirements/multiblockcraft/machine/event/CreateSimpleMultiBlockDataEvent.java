package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleMultiBlockMachineData;

/**
 * 创建机器数据事件。此事件仅当玩家打开一个新的多方块机器的GUI界面时产生。
 */
public class CreateSimpleMultiBlockDataEvent extends CreateMachineDataEvent {

    public CreateSimpleMultiBlockDataEvent(Machine machine, String identifier, SimpleMultiBlockMachineData machineData) {
        super(machine, identifier, machineData);
    }

    public CreateSimpleMultiBlockDataEvent(boolean isAsync, Machine machine, String identifier, SimpleMultiBlockMachineData machineData) {
        super(isAsync, machine, identifier, machineData);
    }

    @Override
    public SimpleMultiBlockMachineData getMachineData() {
        return (SimpleMultiBlockMachineData) super.getMachineData();
    }

    public void setMachineData(SimpleMultiBlockMachineData machineData) {
        this.machineData = machineData;
    }
}

