package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataStorable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleUpdatableMachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.CreateSimpleUpdatableDataEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.OpenMachineGuiEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.SimpleCraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleAbstractRecipe;

import java.io.File;
import java.util.UUID;

public class SimpleTimeCraftTable extends SimpleMachine {
    private final Creative creative;

    public SimpleTimeCraftTable() {
        creative = new Creative(this);
    }

    public SimpleTimeCraftTable(ConfigurationSection section) {
        super(section);
        creative = new Creative(this);
    }

    @Override
    public void open(Player player, String identifier) {
        OpenMachineGuiEvent event = new OpenMachineGuiEvent(this, player, identifier);
        MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        Holder holder = new Holder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(this);
        holder.setIdentifier(identifier);

        SimpleUpdatableMachineData data = (SimpleUpdatableMachineData) MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().loadMachineData(identifier);
        if (data == null) {
            data = new SimpleUpdatableMachineData(this);
            data.setIdentifier(identifier);

            CreateSimpleUpdatableDataEvent dataEvent = new CreateSimpleUpdatableDataEvent(this, identifier, data);
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(dataEvent);
            data = dataEvent.getMachineData();

            MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().storeMachineData(data);
        }
        holder.setMachineData(data);
        player.openInventory(inv);
        holder.start();
    }

    @Override
    public void createRecipe(Player player) {
        Holder holder = new Holder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(creative);
        player.openInventory(inv);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        ((MachineDataStorable) ((Holder) inv.getHolder()).getMachineData()).forEach(inv::setItem);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) return;

        int slot = event.getRawSlot();
        Inventory inv = event.getInventory();
        SimpleCraftHolder holder = (SimpleCraftHolder) inv.getHolder();
        SimpleUpdatableMachineData data = (SimpleUpdatableMachineData) holder.getMachineData();

        if (emptySlots.contains(slot)) return;
        boolean clickedInputs = inputSlots.contains(slot);
        boolean clickedOutputs = outputSlots.contains(slot);
        if (slot < inventory.getSize() && !clickedInputs && !emptySlots.contains(slot) && !clickedOutputs) {
            isClickedButton(slot, inv, data);
            event.setCancelled(true);
            return;
        }

        if (slot >= inventory.getSize() &&
                (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            syncItem(data, inv);
            return;
        }

        if (clickedOutputs) {
            ItemStack item = inv.getItem(slot);
            ItemStack itemOnCursor = event.getWhoClicked().getItemOnCursor();
            if (item == null || itemOnCursor != null && itemOnCursor.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }
            syncItem(data, inv);
            return;
        }

        if (clickedInputs) {
            syncItem(data, inv);
            return;
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        for (Integer rawSlot : event.getRawSlots()) {
            if (emptySlots.contains(rawSlot)) continue;
            if (inputSlots.contains(rawSlot)) continue;
            if (rawSlot >= inventory.getSize()) continue;
            event.setCancelled(true);
            return;
        }
        Inventory inv = event.getInventory();
        SimpleCraftHolder holder = (SimpleCraftHolder) inv.getHolder();
        SimpleUpdatableMachineData data = (SimpleUpdatableMachineData) holder.getMachineData();
        syncItem(data, inv);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Holder holder = (Holder) inv.getHolder();
        holder.close();
    }

    private void syncItem(SimpleUpdatableMachineData data, Inventory inv) {
        data.getWriteLock().lock();
        inputSlots.forEach(i -> inv.setItem(i, data.getItem(i)));
        outputSlots.forEach(i -> inv.setItem(i, data.getItem(i)));
        runTask(() -> {
            try {
                inputSlots.forEach(i -> data.setItem(i, inv.getItem(i)));
                outputSlots.forEach(i -> data.setItem(i, inv.getItem(i)));
            } finally {
                data.getWriteLock().unlock();
            }
        });
    }

    private static class Holder extends SimpleCraftHolder {
        BukkitTask bukkitTask;

        synchronized void start() {
            bukkitTask = MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(
                    MultiBlockCraftExtension.getInstance().getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            SimpleUpdatableMachineData data = (SimpleUpdatableMachineData) getMachineData();
                            data.getLock().lock();
                            try {
                                getMachine().getInputSlots().forEach(it -> {
                                    getInventory().setItem(it, data.getItem(it));
                                });
                                getMachine().getOutputSlots().forEach(it -> {
                                    getInventory().setItem(it, data.getItem(it));
                                });
                                getMachine().getFuncItems().forEach((slot, func) -> {
                                    ItemStack item = getInventory().getItem(slot);
                                    if (item != null && item.getType() != Material.AIR) {
                                        item = func.update(item, getMachineData());
                                        getInventory().setItem(slot, item);
                                    }
                                });
                            } finally {
                                data.getLock().unlock();
                            }
                        }
                    }, 4L, 4L
            );
            DebugLogger.debug("物品栏异步任务开启");
        }

        synchronized void close() {
            if (bukkitTask != null) {
                bukkitTask.cancel();
                DebugLogger.debug("物品栏异步任务关闭");
            }
        }
    }

    private static class Creative extends SimpleMachine {
        private final SimpleMachine instance;

        private Creative(SimpleMachine instance) {
            this.instance = instance;
        }

        @Override
        public void onClose(InventoryCloseEvent event) {
            HumanEntity player = event.getPlayer();
            Inventory inv = event.getInventory();

            boolean inputsAllEmpty = true;
            SimpleItem[] inputs = new SimpleItem[instance.getInputSlots().size()];
            int idx = 0;
            for (Integer inputSlot : instance.getInputSlots()) {
                inputs[idx] = new SimpleItem(SimpleItem.TYPE_NREQ, inv.getItem(inputSlot));
                if (inputs[idx].getItemStack() != null) inputsAllEmpty = false;
                ++idx;
            }

            idx = 0;
            boolean outputsAllEmpty = true;
            SimpleItem[] outputs = new SimpleItem[instance.getOutputSlots().size()];
            for (Integer outputSlot : instance.getOutputSlots()) {
                outputs[idx] = new SimpleItem(SimpleItem.TYPE_NREQ, inv.getItem(outputSlot));
                if (outputs[idx].getItemStack() != null) outputsAllEmpty = false;
                ++idx;
            }

            if (inputsAllEmpty && outputsAllEmpty) return;

            Recipe recipe = new SimpleAbstractRecipe() {
                {
                    this.rawInputs = inputs;
                    this.rawOutputs = outputs;
                    this.id = UUID.randomUUID().toString();
                    this.name = this.id;
                }
            };
            File file = MultiBlockCraftExtension.getInstance().getMachineService().createRecipe(instance.getId(), recipe);
            player.sendMessage("Save to: " + file);
        }
    }
}
