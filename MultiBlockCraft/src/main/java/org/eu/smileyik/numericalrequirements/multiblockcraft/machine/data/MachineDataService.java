package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data;

public interface MachineDataService {
    MachineData loadMachineData(String identifier);

    void storeMachineData(MachineData machineData);

    void removeMachineData(String identifier);

    void save();
}
