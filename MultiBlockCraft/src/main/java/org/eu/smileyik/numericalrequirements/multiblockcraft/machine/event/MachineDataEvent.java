package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;

public class MachineDataEvent extends MachineEvent {
    private final String identifier;
    protected MachineData machineData;

    public MachineDataEvent(Machine machine, String identifier, MachineData machineData) {
        super(machine);
        this.identifier = identifier;
        this.machineData = machineData;
    }

    public MachineDataEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData) {
        super(isAsync, machine);
        this.identifier = identifier;
        this.machineData = machineData;
    }


    public MachineData getMachineData() {
        return machineData;
    }

    public String getIdentifier() {
        return identifier;
    }
}
