package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

/**
 * 寻找匹配配方前置事件。当机器开始寻找配方时触发。
 * 若在事件发生时调用 setRecipe 方法，则会跳过机器本身寻找配方的过程。
 */
public class PreFindRecipeEvent extends MachineEvent {
    private Recipe recipe;
    private final ItemStack[] inputs;

    public PreFindRecipeEvent(Machine machine, ItemStack[] inputs) {
        super(machine);
        this.inputs = inputs;
    }

    public PreFindRecipeEvent(boolean isAsync, Machine machine, ItemStack[] inputs) {
        super(isAsync, machine);
        this.inputs = inputs;
    }

    /**
     * 设置合成的配方。若配方不为空则将直接应用此配方。
     * @param recipe
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * 获取当前设定的配方。
     * @return
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * 获取参与合成的物品，存在物品次序，且内部物品可能为null
     * @return
     */
    public ItemStack[] getInputs() {
        return inputs;
    }
}
