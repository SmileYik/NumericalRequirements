package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.core.event.Event;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public class MachineEvent extends Event {
    private final Machine machine;

    public MachineEvent(Machine machine) {
        this.machine = machine;
    }

    public MachineEvent(boolean isAsync, Machine machine) {
        super(isAsync);
        this.machine = machine;
    }

    /**
     * 获取目标机器
     * @return
     */
    public Machine getMachine() {
        return machine;
    }
}
