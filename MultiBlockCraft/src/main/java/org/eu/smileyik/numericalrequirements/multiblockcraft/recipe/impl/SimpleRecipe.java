package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class SimpleRecipe extends SimpleAbstractRecipe {
    protected ItemStack[] outputs;

    @Override
    public Collection<ItemStack> getInputs() {
        lazyMap();
        return inputAmountMap.keySet();
    }

    @Override
    public Collection<ItemStack> getOutputs() {
        lazyMap();
        return Arrays.asList(outputs);
    }

    @Override
    public boolean isMatch(ItemStack[] inputs) {
        // 比对提供物品以判断是否能够满足配方要求
        if (inputs == null) return false;
        lazyMap();
        Map<ItemStack, Integer> map = mapItemAmount(inputs);
        if (map.size() < this.inputAmountMap.size()) return false;
        for (Map.Entry<ItemStack, Integer> entry : this.inputAmountMap.entrySet()) {
            if (map.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
        }
        return true;
    }

    protected void lazyMap() {
        if (outputs == null) {
            this.outputs = Arrays.stream(rawOutputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new);
        }
    }
}
