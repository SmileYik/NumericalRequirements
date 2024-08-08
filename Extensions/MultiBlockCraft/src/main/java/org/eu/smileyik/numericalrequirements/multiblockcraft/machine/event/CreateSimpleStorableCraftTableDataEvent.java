package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleStorableCraftTable;

/**
 * 创建机器数据事件。此事件仅当玩家打开一个新的可存储物品的合成台机器的GUI界面时产生。
 */
public class CreateSimpleStorableCraftTableDataEvent extends CreateMachineDataEvent {

    public CreateSimpleStorableCraftTableDataEvent(Machine machine, String identifier, SimpleStorableCraftTable.Data machineData) {
        super(machine, identifier, machineData);
    }

    public CreateSimpleStorableCraftTableDataEvent(boolean isAsync, Machine machine, String identifier, SimpleStorableCraftTable.Data machineData) {
        super(isAsync, machine, identifier, machineData);
    }

    @Override
    public SimpleStorableCraftTable.Data getMachineData() {
        return (SimpleStorableCraftTable.Data) super.getMachineData();
    }

    public void setMachineData(SimpleStorableCraftTable.Data machineData) {
        this.machineData = machineData;
    }
}
