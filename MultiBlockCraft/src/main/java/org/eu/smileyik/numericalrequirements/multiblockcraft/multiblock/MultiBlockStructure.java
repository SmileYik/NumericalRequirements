package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class MultiBlockStructure {
    private String material;
    private final MultiBlockStructure[] nears = new MultiBlockStructure[6];

    public void store(ConfigurationSection section) {
        section.set("material", material);
        if (getUp() != null) getUp().store(section.createSection("up"));
        if (getDown() != null) getDown().store(section.createSection("down"));
        if (getLeft() != null) getLeft().store(section.createSection("left"));
        if (getRight() != null) getRight().store(section.createSection("right"));
        if (getFront() != null) getFront().store(section.createSection("front"));
        if (getBack() != null) getBack().store(section.createSection("back"));
    }

    public void load(ConfigurationSection section) {
        material = section.getString("material");
        if (section.contains("up")) {
            nears[0] = new MultiBlockStructure();
            nears[0].load(section.getConfigurationSection("up"));
        }
        if (section.contains("down")) {
            nears[1] = new MultiBlockStructure();
            nears[1].load(section.getConfigurationSection("down"));
        }
        if (section.contains("left")) {
            nears[2] = new MultiBlockStructure();
            nears[2].load(section.getConfigurationSection("left"));
        }
        if (section.contains("right")) {
            nears[3] = new MultiBlockStructure();
            nears[3].load(section.getConfigurationSection("right"));
        }
        if (section.contains("front")) {
            nears[4] = new MultiBlockStructure();
            nears[4].load(section.getConfigurationSection("front"));
        }
        if (section.contains("back")) {
            nears[5] = new MultiBlockStructure();
            nears[5].load(section.getConfigurationSection("back"));
        }
    }

    public boolean isSameBlock(Block block) {
        return block != null && material.equalsIgnoreCase(block.getType().name());
    }

    public void setBlock(Block block) {
        if (block == null) return;
        material = block.getType().name();
    }

    public MultiBlockStructure set(int way, Block block) {
        if (nears[way] == null) nears[way] = new MultiBlockStructure();
        nears[way].setBlock(block);
        return nears[way];
    }

    public MultiBlockStructure getNear(int way) {
        return nears[way];
    }

    public MultiBlockStructure[] getNears() {
        return nears;
    }

    public MultiBlockStructure getUp() {
        return nears[0];
    }

    public MultiBlockStructure getDown() {
        return nears[1];
    }

    public MultiBlockStructure getLeft() {
        return nears[2];
    }

    public MultiBlockStructure getRight() {
        return nears[3];
    }

    public MultiBlockStructure getFront() {
        return nears[4];
    }

    public MultiBlockStructure getBack() {
        return nears[5];
    }
}
