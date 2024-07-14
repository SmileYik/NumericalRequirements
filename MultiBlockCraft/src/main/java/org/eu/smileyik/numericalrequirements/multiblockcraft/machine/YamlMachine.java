package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;

import java.util.ArrayList;
import java.util.List;

public abstract class YamlMachine implements Machine {
    protected String id;
    protected String name;
    protected String title;

    protected SimpleItem machineItem;
    protected List<Integer> inputSlots;
    protected List<Integer> outputSlots;
    protected List<Integer> emptySlots;

    protected Inventory inventory;

    public YamlMachine() {

    }

    public YamlMachine(ConfigurationSection section) {
        id = section.getString("id");
        name = section.getString("name");
        title = section.getString("title");

        machineItem = SimpleItem.load(section.getConfigurationSection("machine-item"));
        inputSlots = section.getIntegerList("input-slots");
        outputSlots = section.getIntegerList("output-slots");
        emptySlots = section.getIntegerList("empty-slots");
        ConfigurationSection inv = section.getConfigurationSection("inv-items");
        inventory = NumericalRequirements.getPlugin().getServer().createInventory(null, section.getInt("inv-size"));
        for (String key : inv.getKeys(false)) {
            int i = Integer.parseInt(key);
            SimpleItem item = SimpleItem.load(inv.getConfigurationSection(key));
            ItemStack itemStack = item.getItemStack();
            if (itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                lore.add("§7 ");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i, itemStack);
        }
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

    @Override
    public ItemStack getMachineItem() {
        return machineItem.getItemStack().clone();
    }
}
