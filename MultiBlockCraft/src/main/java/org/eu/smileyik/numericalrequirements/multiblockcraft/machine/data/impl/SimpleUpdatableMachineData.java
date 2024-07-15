package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

public class SimpleUpdatableMachineData extends SimpleStorableMachineData implements MachineDataUpdatable {
    private boolean enable;
    private String recipeId;
    private long finishedTimestamp;

    public SimpleUpdatableMachineData(Machine machine) {
        super(machine);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public Recipe getRecipe() {
        return recipeId == null ? null : machine.findRecipe(recipeId);
    }

    @Override
    public double getRemainingTime() {
        return (System.nanoTime() - finishedTimestamp) / 1E9;
    }

    @Override
    public double getCraftedTime() {
        return getTotalTime() - getRemainingTime();
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public long period() {
        return 0;
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        super.load(section);
        this.enable = section.getBoolean("enable", false);
        this.recipeId = section.getString("recipe", null);
        this.finishedTimestamp = section.getLong("finishedTimestamp");
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        super.store(section);
        section.set("enable", enable);
        section.set("recipe", recipeId);
        section.set("finishedTimestamp", finishedTimestamp);
    }
}
