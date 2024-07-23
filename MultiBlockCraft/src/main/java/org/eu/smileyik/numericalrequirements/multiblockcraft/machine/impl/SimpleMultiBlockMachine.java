package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MultiBlockMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl.SimpleMultiBlockMachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.SimpleCraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.Structure;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.StructureMainBlock;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleAbstractRecipe;

import java.io.File;
import java.util.UUID;

public class SimpleMultiBlockMachine extends SimpleMachine implements MultiBlockMachine {
    private StructureMainBlock mainBlock;
    private long checkPeriod;

    public SimpleMultiBlockMachine() {

    }

    public SimpleMultiBlockMachine(ConfigurationSection section) {
        super(section);
        mainBlock = (StructureMainBlock) Structure.newMultiBlockStructure(section.getConfigurationSection("structure"));
        mainBlock.init();
        checkPeriod = section.getLong("check-period", 5000);
    }

    @Override
    public StructureMainBlock getStructure() {
        return mainBlock;
    }

    @Override
    public long getCheckPeriod() {
        return checkPeriod;
    }

    @Override
    public void open(Player player, String identifier) {
        Holder holder = new Holder();
        Inventory inv = NumericalRequirements.getPlugin().getServer().createInventory(holder, inventory.getSize(), title);
        inv.setContents(inventory.getContents());
        holder.setInventory(inv);
        holder.setMachine(this);
        holder.setIdentifier(identifier);

        SimpleMultiBlockMachineData data = (SimpleMultiBlockMachineData) MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().loadMachineData(identifier);
        if (data == null) {
            data = new SimpleMultiBlockMachineData(this);
            data.setIdentifier(identifier);
            MultiBlockCraftExtension.getInstance().getMachineService().getMachineDataService().storeMachineData(data);
        }
        holder.setMachineData(data);
        player.openInventory(inv);
        holder.start();
    }

    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Holder holder = (Holder) inv.getHolder();
        holder.close();
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        for (Integer rawSlot : event.getRawSlots()) {
            if (rawSlot < inventory.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        isClickedButton(event.getRawSlot(), event.getInventory(), ((Holder) event.getInventory().getHolder()).getMachineData());
    }

    private static class Holder extends SimpleCraftHolder {
        BukkitTask bukkitTask;

        synchronized void start() {
            bukkitTask = MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(
                    MultiBlockCraftExtension.getInstance().getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            getMachine().getFuncItems().forEach((slot, func) -> {
                                ItemStack item = getInventory().getItem(slot);
                                if (item != null && item.getType() != Material.AIR) {
                                    item = func.update(item, getMachineData());
                                    getInventory().setItem(slot, item);
                                }
                            });
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
