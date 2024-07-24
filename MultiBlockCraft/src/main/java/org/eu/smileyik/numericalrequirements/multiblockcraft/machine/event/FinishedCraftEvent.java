package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.Collection;
import java.util.List;

/**
 * 当配方完成制造时触发。 部分机器的MachineData可能为null。
 */
public class FinishedCraftEvent extends MachineDataEvent {
    private final Recipe recipe;
    private final List<ItemStack> outputs;

    public FinishedCraftEvent(Machine machine, String identifier, MachineData machineData, Recipe recipe, Collection<ItemStack> outputs) {
        super(machine, identifier, machineData);
        this.recipe = recipe;
        this.outputs = outputs.stream().map(ItemStack::clone).toList();
    }

    public FinishedCraftEvent(boolean isAsync, Machine machine, String identifier, MachineData machineData, Recipe recipe, Collection<ItemStack> outputs) {
        super(isAsync, machine, identifier, machineData);
        this.recipe = recipe;
        this.outputs = outputs.stream().map(ItemStack::clone).toList();
    }

    /**
     * 获取制作的配方。
     * @return
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * 获取制作的产物。
     * @return
     */
    public List<ItemStack> getOutputs() {
        return outputs;
    }
}
