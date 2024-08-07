package org.eu.smileyik.numericalrequirements.core.customblock;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;

public abstract class AbstractRealCustomBlockService extends AbstractCustomBlockService implements Listener {

    @EventHandler(ignoreCancelled = true)
    public synchronized void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Pos pos = new Pos(location);
        CustomBlock customBlockByPos = getCustomBlockByPos(pos);
        if (customBlockByPos == null) return;

        if (breakCustomBlock(customBlockByPos, pos)) {
            event.setCancelled(true);
            customBlockByPos.drop(event.getPlayer(), location);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public synchronized void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        String itemId = ItemService.getItemId(itemInHand);
        if (itemId == null) return;

        CustomBlock customBlock = getCustomBlockByItemId(itemId);
        if (customBlock == null) return;

        Pos pos = new Pos(event.getBlock().getLocation());
        if (placeCustomBlock(customBlock, pos)) {
            event.setCancelled(true);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public synchronized void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Pos pos = new Pos(event.getClickedBlock().getLocation());
        CustomBlock customBlock = getCustomBlockByPos(pos);
        if (customBlock == null) return;

        event.setCancelled(true);
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            customBlock.rightClick(event.getPlayer(), event.getClickedBlock().getLocation(), event.getBlockFace());
        } else {
            customBlock.leftClick(event.getPlayer(), event.getClickedBlock().getLocation(), event.getBlockFace());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        loadChunkBlocks(event.getChunk());
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        unloadChunkBlocks(event.getChunk());
    }
}
