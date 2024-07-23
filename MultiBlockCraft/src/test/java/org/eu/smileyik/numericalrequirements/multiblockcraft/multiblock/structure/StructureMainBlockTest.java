package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;

public class StructureMainBlockTest {
    @Test
    public void loadStructureTest() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(
                StructureMainBlockTest.class.getResourceAsStream("/multi-block-structure-1.yml")
        ));
        MultiBlockStructureMainBlock structure = (MultiBlockStructureMainBlock) Structure.newMultiBlockStructure(config);
        System.out.println(structure);
        YamlConfiguration config2 = new YamlConfiguration();
        structure.store(config2);
        System.out.println(config2.saveToString());
        assert config.saveToString().equals(config2.saveToString());
    }

    @Test
    public void pathTest() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(
                StructureMainBlockTest.class.getResourceAsStream("/multi-block-structure-1.yml")
        ));
        MultiBlockStructureMainBlock structure = (MultiBlockStructureMainBlock) Structure.newMultiBlockStructure(config);
        structure.init();
        System.out.println(structure.getInputPath());
        System.out.println(structure.getOutputPath());
    }
}
