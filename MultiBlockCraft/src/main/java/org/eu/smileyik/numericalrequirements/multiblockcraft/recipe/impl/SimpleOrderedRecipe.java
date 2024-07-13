package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.OrderedRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.util.HexUtil;

import java.util.*;

public class SimpleOrderedRecipe extends SimpleAbstractRecipe implements OrderedRecipe {
    protected Pair<Byte[], ItemStack[]> inputs;
    protected ItemStack[] outputs;
    private String shapeString;

    @Override
    public Collection<ItemStack> getInputs() {
        lazyLoad();
        return List.of(inputs.getSecond());
    }

    @Override
    public Collection<ItemStack> getOutputs() {
        lazyLoad();
        return List.of(outputs);
    }

    @Override
    public boolean isMatch(ItemStack[] inputs) {
        // 调用此方法前必须判断是否满足形状
        lazyLoad();
        if (inputs == null) return false;
        ItemStack[] a = spawnShape(inputs).getSecond();
        ItemStack[] b = this.inputs.getSecond();
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (b[i] == null) {
                if (a[i] != null) return false;
                continue;
            }
            if (!b[i].isSimilar(a[i]) || b[i].getAmount() > a[i].getAmount()) return false;
        }
        return true;
    }

    @Override
    public String getShapeString() {
        lazyLoad();
        return shapeString;
    }

    private void lazyLoad() {
        if (inputs == null) {
            inputs = spawnShape(
                    Arrays.stream(rawInputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new)
            );
            shapeString = HexUtil.bytesToHex(inputs.getFirst());
            inputAmountMap = mapItemAmount(Arrays.stream(rawInputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new));
        }

        if (outputs == null) {
            outputs = Arrays.stream(rawOutputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new);
        }
    }

    public static Pair<Byte[], ItemStack[]> spawnShape(ItemStack[] inputs) {
        byte idx = 0;
        List<Byte> shape = new ArrayList<>();
        List<ItemStack> idItemList = new ArrayList<>();
        Map<ItemStack, Byte> itemIdMap = new HashMap<>();
        for (ItemStack item : inputs) {
            byte id = itemIdMap.getOrDefault(item, idx);
            if (idx == id) {
                itemIdMap.put(item, id);
                idItemList.add(item);
                shape.add(id);
                ++idx;
            }
        }
        return Pair.newUnchangablePair(
                shape.toArray(new Byte[0]),
                idItemList.toArray(new ItemStack[0])
        );
    }
}
