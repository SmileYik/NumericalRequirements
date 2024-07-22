package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class MultiBlockListener implements Listener {
    private boolean flag;
    private MultiBlockStructure structure = null;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!flag) {
            if (structure == null) return;
            Block clickedBlock = event.getClickedBlock();
            if (!structure.isSameBlock(clickedBlock)) return;
            MultiBlockFace[] faces = MultiBlockFace.getByFace(event.getBlockFace());
            if (clickedBlock == null) {
                return;
            }

            for (MultiBlockFace face : faces) {
                boolean flag = true;
                BlockFace[] ways = face.getFaces();

                // create(clickedBlock, ways);

                LinkedList<Pair<MultiBlockStructure, Block>> queue = new LinkedList<>();
                queue.add(new Pair<>(structure, clickedBlock));
                while (!queue.isEmpty() && flag) {
                    Pair<MultiBlockStructure, Block> pair = queue.removeFirst();
                    for (int i = ways.length - 1; i >= 0; i--) {
                        MultiBlockStructure near = pair.getFirst().getNear(i);
                        if (near == null) continue;
                        Block block = pair.getSecond().getRelative(ways[i]);
                        if (near.isSameBlock(block)) {
                            queue.add(new Pair<>(near, block));
                        } else {
                            // not equal
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    System.out.println("Equal structure");
                    return;
                }
            }
            System.out.println("Not equal structure");
            return;
        }


        event.setCancelled(true);

        Block clickedBlock = event.getClickedBlock();
        MultiBlockFace[] faces = MultiBlockFace.getByFace(event.getBlockFace());
        if (clickedBlock == null || faces.length == 0) {
            return;
        }
        boolean flag = true;
        for (MultiBlockFace face : faces) {
            BlockFace[] ways = face.getFaces();
            if (flag) {
                this.structure = create(clickedBlock, ways);
                flag = false;
            } else {
                create(clickedBlock, ways);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("start")) {
            flag = true;
        } else if (event.getMessage().equalsIgnoreCase("stop")) {
            flag = false;
        }
    }

    private MultiBlockStructure create(Block clickedBlock, BlockFace[] ways) {
        LinkedList<Pair<MultiBlockStructure, Block>> queue = new LinkedList<>();
        MultiBlockStructure structure = new MultiBlockStructure();
        structure.setBlock(clickedBlock);
        queue.add(Pair.newPair(structure, clickedBlock));
        Set<Location> checked = new HashSet<>();
        checked.add(clickedBlock.getLocation());
        while (!queue.isEmpty()) {
            Pair<MultiBlockStructure, Block> pair = queue.removeFirst();

            for (int i = ways.length - 1; i >= 0; i--) {
                Block block = pair.getSecond().getRelative(ways[i]);
                if (block != null && !block.isEmpty() && checked.add(block.getLocation())) {
                    queue.add(Pair.newPair(pair.getFirst().set(i, block), block));
                }
            }
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        structure.store(yamlConfiguration);
        System.out.println(yamlConfiguration.saveToString());
        return structure;
    }
}
