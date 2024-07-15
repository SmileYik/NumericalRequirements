package org.eu.smileyik.numericalrequirements.multiblockcraft.data.impl;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.data.MachineDataStorable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SimpleStorableMachineData implements MachineDataStorable {
    protected final Machine machine;
    protected String identifier;
    protected Location location;
    private final Map<Integer, ItemStack> items = new HashMap<>();

    public SimpleStorableMachineData(Machine machine) {
        this.machine = machine;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        this.location = Machine.fromIdentifier(identifier);
    }

    @Override
    public void forEach(BiConsumer<Integer, ItemStack> consumer) {
        items.forEach(consumer);
    }

    @Override
    public synchronized void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    @Override
    public synchronized ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public synchronized void removeItem(int slot) {
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
    public Location getLocation() {
        return location;
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
        this.location = Machine.fromIdentifier(identifier);
        ConfigurationSection ic = section.getConfigurationSection("items");
        ic.getKeys(false).forEach(k -> {
            items.put(Integer.parseInt(k), ic.getItemStack(k, null));
        });
    }
}
