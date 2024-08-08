package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleUpdatableMachineData;

/**
 * 创建机器数据事件。此事件仅当玩家打开一个使用SimpleUpdatableMachineData的新机器的GUI界面时产生。
 */
public class CreateSimpleUpdatableDataEvent extends CreateMachineDataEvent {

    public CreateSimpleUpdatableDataEvent(Machine machine, String identifier, SimpleUpdatableMachineData machineData) {
        super(machine, identifier, machineData);
    }

    public CreateSimpleUpdatableDataEvent(boolean isAsync, Machine machine, String identifier, SimpleUpdatableMachineData machineData) {
        super(isAsync, machine, identifier, machineData);
    }

    @Override
    public SimpleUpdatableMachineData getMachineData() {
        return (SimpleUpdatableMachineData) super.getMachineData();
    }

    public void setMachineData(SimpleUpdatableMachineData machineData) {
        this.machineData = machineData;
    }
}
