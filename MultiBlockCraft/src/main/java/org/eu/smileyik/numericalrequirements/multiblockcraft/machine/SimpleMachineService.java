package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleMachineService implements MachineService {
    private Map<String, Machine> machines = new HashMap<>();
    private Map<String, File> machinesFiles = new HashMap<>();

    public SimpleMachineService(MultiBlockCraftExtension extension) {
        File dataFolder = extension.getDataFolder();
        loadMachines(dataFolder);
    }

    @Override
    public Machine getMachine(String id) {
        return machines.get(id);
    }

    @Override
    public File createRecipe(String machineId, Recipe recipe) {
        File file = machinesFiles.get(machineId);
        file = new File(new File(file, "recipes"), recipe.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        recipe.store(config);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private void loadMachines(File folder) {
        for (File dir : Objects.requireNonNull(folder.listFiles())) {
            if (!dir.isDirectory()) continue;
            File file = new File(dir, "machine.yml");
            if (!file.exists()) continue;
            ConfigurationSection machine = YamlConfiguration.loadConfiguration(file);

            String type = machine.getString("type");
            if (type == null) continue;
            Machine m = null;
            try {
                m = (Machine) Class.forName(type).getDeclaredConstructor(ConfigurationSection.class).newInstance(machine);
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
            if (m == null) continue;

            System.out.println("loaded machine " + m.getId());
            machines.put(m.getId(), m);
            machinesFiles.put(m.getId(), dir);

            file = new File(dir, "recipes");
            if (!file.exists()) continue;

            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (listFile.isFile() && listFile.getName().toLowerCase().endsWith(".yml")) {
                    ConfigurationSection r = YamlConfiguration.loadConfiguration(listFile);
                    type = r.getString("type");
                    if (type == null) continue;
                    Recipe recipe = null;
                    try {
                        recipe = (Recipe) Class.forName(type).getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |
                             InvocationTargetException | IllegalAccessException | ClassCastException e) {
                        e.printStackTrace();
                    }
                    if (recipe == null) continue;
                    recipe.load(r);
                    m.addRecipe(recipe);
                }
            }
        }
    }
}
