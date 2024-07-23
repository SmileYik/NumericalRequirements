package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;

public class MultiBlockStructure implements Structure {
    private String material;
    private final Structure[] nears = new Structure[6];

    @Override
    public void store(ConfigurationSection section) {
        section.set("material", material);
        if (getClass() != MultiBlockStructure.class) section.set("type", getClass().getName());
        for (StructureFace face : StructureFace.values()) {
            if (nears[face.getIndex()] != null) nears[face.getIndex()].store(section.createSection(face.getName()));
        }
    }

    @Override
    public void load(ConfigurationSection section) {
        material = section.getString("material");
        nears[0] = Structure.newMultiBlockStructure(section.getConfigurationSection("up"));
        nears[1] = Structure.newMultiBlockStructure(section.getConfigurationSection("down"));
        nears[2] = Structure.newMultiBlockStructure(section.getConfigurationSection("left"));
        nears[3] = Structure.newMultiBlockStructure(section.getConfigurationSection("right"));
        nears[4] = Structure.newMultiBlockStructure(section.getConfigurationSection("front"));
        nears[5] = Structure.newMultiBlockStructure(section.getConfigurationSection("back"));
    }

    @Override
    public boolean isSameBlock(Block block) {
        return block != null && material.equalsIgnoreCase(block.getType().name());
    }

    @Override
    public void setBlock(Block block) {
        if (block == null) return;
        material = block.getType().name();
    }

    @Override
    public Structure set(int way, Block block) {
        if (nears[way] == null) nears[way] = new MultiBlockStructure();
        nears[way].setBlock(block);
        return nears[way];
    }

    @Override
    public Structure set(StructureFace structureFace, Block block) {
        return set(structureFace.getIndex(), block);
    }

    @Override
    public Structure getNear(int way) {
        return nears[way];
    }

    @Override
    public Structure getNear(StructureFace structureFace) {
        return nears[structureFace.getIndex()];
    }

    @Override
    public Structure[] getNears() {
        return nears;
    }

    @Override
    public String toString() {
        return "MultiBlockStructure{" +
                "material='" + material + '\'' +
                ", nears=" + Arrays.toString(nears) +
                '}';
    }

    @Override
    public Iterator<Pair<StructureFace, Structure>> iterator() {
        return new Iter(this);
    }

    @Override
    public void forEach(BiConsumer<StructureFace, Structure> biConsumer) {
        forEach(it -> biConsumer.accept(it.getFirst(), it.getSecond()));
    }

    private static class Iter implements Iterator<Pair<StructureFace, Structure>> {
        private final MultiBlockStructure instance;
        private int index = 0;

        private Iter(MultiBlockStructure instance) {
            this.instance = instance;
        }

        @Override
        public boolean hasNext() {
            while (index < 6 && instance.nears[index] == null) index++;
            return index != 6;
        }

        @Override
        public Pair<StructureFace, Structure> next() {
            try {
                return Pair.newUnchangablePair(StructureFace.fromIndex(index), instance.nears[index]);
            } finally {
                ++index;
            }
        }
    }
}
