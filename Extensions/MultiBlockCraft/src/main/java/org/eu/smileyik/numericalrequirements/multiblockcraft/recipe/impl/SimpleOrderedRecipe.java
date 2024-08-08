package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.OrderedRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeTakeItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.util.HexUtil;

import java.util.Arrays;
import java.util.Collection;

public class SimpleOrderedRecipe extends SimpleAbstractRecipe implements OrderedRecipe {
    protected ItemStack[] rawInputs;
    protected Pair<Byte[], ItemStack[]> inputs;
    protected ItemStack[] outputs;
    private String shapeString;

    @Override
    public Collection<ItemStack> getInputs() {
        lazyLoad();
        return Arrays.asList(inputs.getSecond());
    }

    @Override
    public Collection<ItemStack> getOutputs() {
        lazyLoad();
        return Arrays.asList(outputs);
    }

    @Override
    public boolean isMatch(ItemStack[] inputs) {
        // 调用此方法前必须判断是否满足形状
        if (inputs == null) return false;
        ItemStack[] a = OrderedRecipe.spawnShape(this, inputs).getSecond();
        return doIsMatch(a);
    }

    @Override
    public boolean isMatch(Pair<Byte[], ItemStack[]> pair) {
        return doIsMatch(pair.getSecond());
    }

    private boolean doIsMatch(ItemStack[] a) {
        lazyLoad();
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

    @Override
    public void takeInputs(ItemStack[] inputs) {
        long id = RecipeTakeItemEvent.nextId();
        int size = this.rawInputs.length - 1;
        boolean latest = true, triggered = false;
        for (int i = size; i >= 0; i--) {
            if (inputs[i] == null) continue;

            // call recipe take item event
            latest = i != 0;
            triggered = true;
            RecipeTakeItemEvent event = new RecipeTakeItemEvent(
                    !MultiBlockCraftExtension.getInstance().getPlugin().getServer().isPrimaryThread(),
                    this, id, i, size - i, size, inputs[i]
            );
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
            inputs[i] = event.getItem();
            if (event.isCancelled()) continue;

            inputs[i].setAmount(inputs[i].getAmount() - this.rawInputs[i].getAmount());
        }

        if (triggered && latest) {
            RecipeTakeItemEvent event = new RecipeTakeItemEvent(
                    !MultiBlockCraftExtension.getInstance().getPlugin().getServer().isPrimaryThread(),
                    this, id, size, size, size, null
            );
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
        }
    }

    private void lazyLoad() {
        if (inputs == null) {
            rawInputs = Arrays.stream(super.rawInputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new);
            inputs = OrderedRecipe.spawnShape(this, rawInputs);
            shapeString = HexUtil.bytesToHex(inputs.getFirst());
            inputAmountMap = mapItemAmount(rawInputs);
        }

        if (outputs == null) {
            outputs = Arrays.stream(rawOutputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new);
        }
    }
}
