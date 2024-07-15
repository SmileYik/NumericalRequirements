package org.eu.smileyik.numericalrequirements.multiblockcraft.data.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.data.MachineDataStorable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleStorableMachineData implements MachineDataStorable {
    protected final Machine machine;
    protected String identifier;
    private Map<Integer, ItemStack> items;

    public SimpleStorableMachineData(Machine machine) {
        this.machine = machine;
    }

    @Override
    public synchronized void setItem(int slot, ItemStack item) {
        if (items == null) items = new HashMap<>();
        if (item == null) {
            items.remove(slot);
            return;
        }
        items.put(slot, item);
    }

    @Override
    public synchronized ItemStack getItem(int slot) {
        if (items == null) items = new HashMap<>();
        return items.get(slot);
    }

    @Override
    public synchronized void removeItem(int slot) {
        if (items == null) items = new HashMap<>();
        items.remove(slot);
    }

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        section.set("identifier", identifier);
        section.set("items", items);
        section.set("type", getClass().getName());
        section.set("machine", machine.getId());
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        identifier = section.getString("identifier");
        items = new LinkedHashMap<>();
        ConfigurationSection ic = section.getConfigurationSection("items");
        ic.getKeys(false).forEach(k -> {
            items.put(Integer.parseInt(k), ic.getItemStack(k, null));
        });
    }
}
