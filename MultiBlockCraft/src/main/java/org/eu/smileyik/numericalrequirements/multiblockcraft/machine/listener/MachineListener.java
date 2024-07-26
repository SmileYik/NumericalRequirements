package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.CraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineLoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineNBTTag;
import org.eu.smileyik.numericalrequirements.nms.NMS;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MachineListener implements Listener {
    public static final ReentrantReadWriteLock CONTAINER_LOCK = new ReentrantReadWriteLock(true);
    public static final String MULTI_BLOCK_MACHINE_CONTAINER_KEY = "NREQ_MB_CONTAINER";


    private final MachineLoreTag machineLoreTag;
    private final MachineNBTTag machineNBTTag;
    private final MachineService machineService = MultiBlockCraftExtension.getInstance().getMachineService();

    public MachineListener(MachineLoreTag machineLoreTag, MachineNBTTag machineNBTTag) {
        this.machineLoreTag = machineLoreTag;
        this.machineNBTTag = machineNBTTag;
    }

    private String getMachineId(ItemStack itemStack) {
        if (itemStack == null) return null;
        String id = machineNBTTag.getValue(itemStack);
        if (id != null) return id;
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return null;
        for (String s : itemStack.getItemMeta().getLore()) {
            if (!machineLoreTag.matches(s)) {
                continue;
            }
            LoreValue value = machineLoreTag.getValue(s);
            return value.get(0);
        }
        return null;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!event.canBuild()) return;
        String machineId = getMachineId(event.getItemInHand());
        Block blockPlaced = event.getBlockPlaced();
        machineService.setMachineMetadata(blockPlaced, "machine", machineId);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String machineId = machineService.delMachineMetadata(block, "machine");
        machineService.getMachineDataService().removeMachineData(Machine.getIdentifier(block.getLocation()));

        if (machineId == null) return;
        Machine machine = MultiBlockCraftExtension.getInstance().getMachineService().getMachine(machineId);
        if (machine == null) return;
        ItemStack machineItem = machine.getMachineItem();
        block.getWorld().dropItemNaturally(block.getLocation(), machineItem);
        event.setDropItems(false);
        event.setExpToDrop(0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking()) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        String machineId = machineService.getMachineMetadata(clickedBlock, "machine");
        Machine machine = MultiBlockCraftExtension.getInstance().getMachineService().getMachine(machineId);
        if (machine == null) return;
        event.setCancelled(true);
        String identifier = Machine.getIdentifier(clickedBlock.getLocation());
        NumericalRequirements.getPlugin().getServer().getScheduler().runTask(
                MultiBlockCraftExtension.getInstance().getPlugin(), () -> {
                    machine.open(event.getPlayer(), identifier);
                }
        );
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof CraftHolder) {
            ((CraftHolder) holder).getMachine().onClick(event);
            return;
        }

        if (NMS.MIDDLE_VERSION == 7) {
            CONTAINER_LOCK.writeLock().lock();
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                    MultiBlockCraftExtension.getInstance().getPlugin(), () -> CONTAINER_LOCK.writeLock().unlock()
            );
            return;
        }

        Location location = inv.getLocation();
        if (location == null) return;
        Block block = location.getBlock();
        if (block.hasMetadata(MULTI_BLOCK_MACHINE_CONTAINER_KEY)) {
            CONTAINER_LOCK.writeLock().lock();
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                    MultiBlockCraftExtension.getInstance().getPlugin(), () -> CONTAINER_LOCK.writeLock().unlock()
            );
        }
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event) {
        Inventory inv = event.getInventory();
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof CraftHolder) {
            ((CraftHolder) holder).getMachine().onDrag(event);
            return;
        }

        if (NMS.MIDDLE_VERSION == 7) {
            CONTAINER_LOCK.writeLock().lock();
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                    MultiBlockCraftExtension.getInstance().getPlugin(), () -> CONTAINER_LOCK.writeLock().unlock()
            );
            return;
        }

        Location location = inv.getLocation();
        if (location == null) return;
        Block block = location.getBlock();
        if (block.hasMetadata(MULTI_BLOCK_MACHINE_CONTAINER_KEY)) {
            CONTAINER_LOCK.writeLock().lock();
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                    MultiBlockCraftExtension.getInstance().getPlugin(), () -> CONTAINER_LOCK.writeLock().unlock()
            );
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CraftHolder) {
            ((CraftHolder) holder).getMachine().onClose(event);
        }
    }
}
