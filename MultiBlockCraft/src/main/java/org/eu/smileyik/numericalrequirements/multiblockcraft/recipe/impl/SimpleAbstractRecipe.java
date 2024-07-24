package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeFormatItemEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event.RecipeTakeItemEvent;

import java.util.*;

public abstract class SimpleAbstractRecipe implements Recipe {
    protected String id;
    protected String name;

    protected SimpleItem[] rawInputs;
    protected SimpleItem[] rawOutputs;

    protected ItemStack[] displayedOutput;

    protected Map<ItemStack, Integer> inputAmountMap;
    protected ConfigurationSection attribute;

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
        section.set("attribute", attribute);
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

        displayedOutput = Arrays.stream(rawOutputs).map(it -> {
            ItemStack itemStack = it.getItemStack();
            if (itemStack == null) return null;
            itemStack = itemStack.clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            lore.add(I18N.tr("extension.multi-block-craft.recipe.displayed-lore"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }).toArray(ItemStack[]::new);

        attribute = section.getConfigurationSection("attribute");
        if (attribute == null) attribute = new YamlConfiguration();
    }

    @Override
    public void takeInputs(ItemStack[] inputs) {
        Map<ItemStack, Integer> inputAmountMap = new HashMap<>(this.inputAmountMap);
        long id = RecipeTakeItemEvent.nextId();
        int size = inputs.length - 1;
        for (int i = 0; i <= size; i++) {
            ItemStack item = inputs[i];
            if (inputAmountMap.isEmpty()) break;
            if (item == null) continue;

            // call recipe take item event
            RecipeTakeItemEvent event = new RecipeTakeItemEvent(
                    !MultiBlockCraftExtension.getInstance().getPlugin().getServer().isPrimaryThread(),
                    this, id, i, i, size, item
            );
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
            inputs[i] = event.getItem();
            if (event.isCancelled()) continue;

            ItemStack clone = item.clone();
            clone.setAmount(1);
            Integer needAmount = inputAmountMap.getOrDefault(clone, 0);
            if (needAmount == 0) continue;
            int itemAmount = item.getAmount();
            if (itemAmount < needAmount) {
                inputAmountMap.put(clone, needAmount - itemAmount);
                item.setAmount(0);
            } else {
                item.setAmount(itemAmount - needAmount);
                inputAmountMap.remove(clone);
            }
        }
    }

    @Override
    public ItemStack[] getDisplayedOutput() {
        return displayedOutput;
    }

    @Override
    public ConfigurationSection getAttribute() {
        return attribute;
    }

    protected Map<ItemStack, Integer> mapItemAmount(ItemStack[] inputs) {
        final long eventId = RecipeFormatItemEvent.nextId();

        Map<ItemStack, Integer> map = new HashMap<>();
        int size = inputs.length - 1;
        for (int i = 0; i <= size; i++) {
            ItemStack item = inputs[i];
            if (item == null) continue;
            item = item.clone();

            // call recipe format item event
            RecipeFormatItemEvent event = new RecipeFormatItemEvent(this, eventId, i, i, size, item);
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
            item = event.getItem();
            if (item == null) continue;

            int amount = item.getAmount();
            if (amount != 1) item.setAmount(1);

            map.put(item, map.getOrDefault(item, 0) + amount);
        }
        return map;
    }
}
