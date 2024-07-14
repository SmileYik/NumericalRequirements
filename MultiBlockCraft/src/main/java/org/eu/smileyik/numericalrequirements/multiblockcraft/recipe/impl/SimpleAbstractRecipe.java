package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.*;

public abstract class SimpleAbstractRecipe implements Recipe {
    protected String id;
    protected String name;

    protected SimpleItem[] rawInputs;
    protected SimpleItem[] rawOutputs;

    protected Map<ItemStack, Integer> inputAmountMap;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isMatch(Collection<ItemStack> inputs) {
        return isMatch(inputs.toArray(new ItemStack[0]));
    }

    @Override
    public void store(ConfigurationSection section) {
        section.set("name", name);
        section.set("id", id);
        section.set("type", getClass().getName());
        ConfigurationSection s = section.createSection("inputs");
        int idx = 0;
        for (SimpleItem item : rawInputs) {
            item.store(s.createSection("input-" + idx++));
        }
        s = section.createSection("outputs");
        idx = 0;
        for (SimpleItem item : rawOutputs) {
            item.store(s.createSection("output-" + idx++));
        }
    }

    @Override
    public void load(ConfigurationSection section) {
        id = section.getString("id");
        name = section.getString("name");

        List<SimpleItem> inputs = new ArrayList<>();
        ConfigurationSection i = section.getConfigurationSection("inputs");
        i.getKeys(false).forEach(key -> {
            inputs.add(SimpleItem.load(i.getConfigurationSection(key)));
        });
        this.rawInputs = inputs.toArray(new SimpleItem[0]);
        inputAmountMap = mapItemAmount(Arrays.stream(rawInputs).map(SimpleItem::getItemStack).toArray(ItemStack[]::new));

        List<SimpleItem> outputs = new ArrayList<>();
        ConfigurationSection o = section.getConfigurationSection("outputs");
        o.getKeys(false).forEach(key -> {
            outputs.add(SimpleItem.load(o.getConfigurationSection(key)));
        });
        this.rawOutputs = outputs.toArray(new SimpleItem[0]);
    }

    @Override
    public void takeInputs(ItemStack[] inputs) {
        for (ItemStack item : inputs) {
            if (item == null) continue;
            ItemStack clone = item.clone();
            clone.setAmount(1);
            Integer amount = this.inputAmountMap.getOrDefault(clone, 0);
            item.setAmount(item.getAmount() - amount);
        }
    }

    protected Map<ItemStack, Integer> mapItemAmount(ItemStack[] inputs) {
        Map<ItemStack, Integer> map = new HashMap<>();
        for (ItemStack item : inputs) {
            if (item == null) continue;
            int amount = item.getAmount();
            if (amount != 1) {
                item = item.clone();
                item.setAmount(1);
            }

            map.put(item, map.getOrDefault(item, 0) + amount);
        }
        return map;
    }
}
