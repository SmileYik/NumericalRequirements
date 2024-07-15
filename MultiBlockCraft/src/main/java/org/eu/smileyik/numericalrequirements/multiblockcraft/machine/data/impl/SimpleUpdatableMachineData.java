package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class SimpleUpdatableMachineData extends SimpleStorableMachineData implements MachineDataUpdatable {
    private boolean enable;
    private String recipeId;
    private long finishedTimestamp;
    private boolean changedItems = true;
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public SimpleUpdatableMachineData(Machine machine) {
        super(machine);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public Recipe getRecipe() {
        return recipeId == null ? null : machine.findRecipe(recipeId);
    }

    @Override
    public double getRemainingTime() {
        return recipeId == null ? 0 : ((finishedTimestamp - System.currentTimeMillis()) / 1E9);
    }

    @Override
    public double getCraftedTime() {
        return recipeId == null ? 0 : getTotalTime() - getRemainingTime();
    }

    @Override
    public boolean isRunning() {
        return recipeId != null && machine.findRecipe(recipeId) != null;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        super.setItem(slot, item);
        changedItems = true;
    }

    @Override
    public void removeItem(int slot) {
        super.removeItem(slot);
        changedItems = true;
    }

    @Override
    public boolean update() {
        if (!isEnable()) return false;

        if (recipeId != null) {
            if (getRemainingTime() <= 0) {
                List<Integer> outputSlots = getMachine().getOutputSlots();
                lock.writeLock().lock();
                try {
                    Map<ItemStack, Integer> itemStackIntegerMap = buildItemAmountMap(outputSlots);
                    for (ItemStack output : getRecipe().getOutputs()) {
                        if (output == null) continue;
                        int amount = output.getAmount();
                        ItemStack clone = output.clone();
                        clone.setAmount(1);
                        itemStackIntegerMap.put(clone, itemStackIntegerMap.getOrDefault(clone, 0) + amount);
                    }
                    setItems(outputSlots, itemStackIntegerMap);
                } finally {
                    lock.writeLock().unlock();
                }
                recipeId = null;
            } else {
                return false;
            }
        }

        if (!changedItems) {
            return false;
        }

        lock.writeLock().lock();
        try {
            ItemStack[] inputs = copyItems(getMachine().getInputSlots());
            Recipe recipe = getMachine().findRecipe(inputs);
            if (recipe == null) {
                return false;
            }

            List<Integer> outputSlots = getMachine().getOutputSlots();
            int size = outputSlots.size();
            boolean needStop = false;
            Map<ItemStack, Integer> map = buildItemAmountMap(outputSlots);
            for (ItemStack output : recipe.getOutputs()) {
                if (output == null) continue;
                int amount = output.getAmount();
                ItemStack clone = output.clone();
                clone.setAmount(1);
                int count = map.getOrDefault(clone, 0) + amount;
                if (count > 64) {
                    needStop = true;
                    break;
                }
                map.put(clone, count);
                if (map.size() > size) {
                    needStop = true;
                    break;
                }
            }

            if (needStop) {
                recipeId = null;
                changedItems = false;
                return false;
            }

            recipe.takeInputs(inputs);

            recipeId = recipe.getId();
            System.out.println(recipeId);
            finishedTimestamp = System.currentTimeMillis() + (recipe instanceof TimeRecipe ? (long) (((TimeRecipe) recipe).getTime() * 1000) : 0L);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long period() {
        return 0;
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        super.load(section);
        this.enable = section.getBoolean("enable", false);
        this.recipeId = section.getString("recipe", null);
        this.finishedTimestamp = System.currentTimeMillis() + section.getLong("remainingTime");
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        super.store(section);
        section.set("enable", enable);
        section.set("recipe", recipeId);
        section.set("remainingTime", getRemainingTime());
    }

    protected Map<ItemStack, Integer> buildItemAmountMap(List<Integer> slots) {
        Map<ItemStack, Integer> itemAmountMap = new HashMap<>();
        forEachItem(slots, item -> {
            if (item == null) return;
            int amount = item.getAmount();
            item = item.clone();
            item.setAmount(1);
            itemAmountMap.put(item, itemAmountMap.getOrDefault(item, 0) + amount);
        });
        return itemAmountMap;
    }

    protected void setItems(List<Integer> slots, Map<ItemStack, Integer> itemAmountMap) {
        for (int i : slots) {
            setItem(i, null);
        }
        int idx = 0, size = slots.size();
        for (Map.Entry<ItemStack, Integer> entry : itemAmountMap.entrySet()) {
            if (idx == size) break;
            ItemStack item = entry.getKey().clone();
            item.setAmount(entry.getValue());
            setItem(slots.get(idx++), item);
        }
    }

    protected ItemStack[] copyItems(List<Integer> slots) {
        int idx = 0;
        ItemStack[] items = new ItemStack[slots.size()];
        for (Integer i : slots) {
            items[idx++] = getItem(i);
        }
        return items;
    }

    protected void forEachItem(List<Integer> slots, Consumer<ItemStack> consumer) {
        for (Integer i : slots) {
            consumer.accept(getItem(i));
        }
    }

    public ReentrantReadWriteLock.ReadLock getLock() {
        return lock.readLock();
    }

    public ReentrantReadWriteLock.WriteLock getWriteLock() {
        return lock.writeLock();
    }
}
