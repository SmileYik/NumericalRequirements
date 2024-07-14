package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener;

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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder.CraftHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineLoreTag;

import java.util.List;

public class MachineListener implements Listener {
    private final MachineLoreTag machineLoreTag;

    public MachineListener(MachineLoreTag machineLoreTag) {
        this.machineLoreTag = machineLoreTag;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!event.canBuild()) return;
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand == null || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta().hasLore()) return;
        for (String s : itemInHand.getItemMeta().getLore()) {
            if (!machineLoreTag.matches(s)) {
                continue;
            }
            LoreValue value = machineLoreTag.getValue(s);
            String machineId = value.get(0);
            Block blockPlaced = event.getBlockPlaced();
            blockPlaced.setMetadata("nerq-machine", new FixedMetadataValue(
                    NumericalRequirements.getPlugin(),
                    machineId
            ));
            break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        List<MetadataValue> metadata = block.getMetadata("nerq-machine");
        if (metadata == null || metadata.isEmpty()) return;
        String machineId = metadata.get(0).asString();
        block.removeMetadata("nerq-machine", NumericalRequirements.getPlugin());

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
        if (event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        List<MetadataValue> metadata = clickedBlock.getMetadata("nerq-machine");
        if (metadata == null || metadata.isEmpty()) return;
        String machineId = metadata.get(0).asString();
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
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CraftHolder) {
            ((CraftHolder) holder).getMachine().onClick(event);
        }
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof CraftHolder) {
            ((CraftHolder) holder).getMachine().onDrag(event);
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
