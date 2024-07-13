package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public abstract class YamlMachine implements Machine {
    protected String id;
    protected String name;
    protected String title;

    protected List<Integer> inputSlots;
    protected List<Integer> outputSlots;
    protected List<Integer> emptySlots;

    public YamlMachine() {

    }

    public YamlMachine(ConfigurationSection section) {
        id = section.getString("id");
        name = section.getString("name");
        title = section.getString("title");
        inputSlots = section.getIntegerList("input-slots");
        outputSlots = section.getIntegerList("output-slots");
        emptySlots = section.getIntegerList("empty-slots");
    }

    @Override
    public List<Integer> getInputSlots() {
        return inputSlots;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Integer> getEmptySlots() {
        return emptySlots;
    }

    @Override
    public List<Integer> getOutputSlots() {
        return outputSlots;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
