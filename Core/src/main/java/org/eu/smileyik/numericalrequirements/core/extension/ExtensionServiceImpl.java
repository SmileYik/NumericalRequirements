package org.eu.smileyik.numericalrequirements.core.extension;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.extension.task.ExtensionTask;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ExtensionServiceImpl implements ExtensionService {
    private final static Field infoField;
    static {
        Class<Extension> extensionClass = Extension.class;
        try {
            infoField = extensionClass.getDeclaredField("info");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final NumericalRequirements plugin;
    private final List<Extension> extensionList = new ArrayList<>();
    private final Map<String, ExtensionTask> taskMap = new HashMap<>();

    public ExtensionServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadExtensions() {
        YamlConfiguration embedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(
                ExtensionServiceImpl.class.getResourceAsStream("/extensions.yml")
        ));
        findAndRegisterExtension(embedConfig);

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File extensionFile = new File(dataFolder, "extensions.yml");
        YamlConfiguration outConfig = YamlConfiguration.loadConfiguration(extensionFile);
        findAndRegisterExtension(outConfig);
    }

    private void findAndRegisterExtension(ConfigurationSection sections) {
        for (String key : sections.getKeys(false)) {
            ConfigurationSection section = sections.getConfigurationSection(key);
            String id = key;
            String name = section.getString("name");
            String author = section.getString("author");
            String description = section.getString("description");
            String version = section.getString("version");
            String main = section.getString("main");
            ExtensionDescription info = new ExtensionDescription(
                    id, name, author, version, description
            );
            try {
                if (register(main, info)) {
                    I18N.tr(info.getId() + " be loaded!");
                } else {
                    I18N.tr(info.getId() + " not load!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean register(String main, ExtensionDescription info) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = getClass().getClassLoader().loadClass(main);
        if (!Extension.class.isAssignableFrom(clazz)) {
            I18N.tr(info.getId() + " not an extension!");
            return false;
        }
        Extension extension = (Extension) clazz.getDeclaredConstructor().newInstance();
        infoField.setAccessible(true);
        infoField.set(extension, info);
        infoField.setAccessible(false);
        return register(extension);
    }

    @Override
    public synchronized boolean register(Extension extension) {
        for (Extension e : extensionList) {
            if (e.getInfo().getId().equals(extension.getInfo().getId())) {
                return false;
            }
        }
        extension.setPlugin(plugin);
        extensionList.add(extension);
        extension.onEnable();
        return true;
    }

    @Override
    public synchronized Extension getExtensionById(String id) {
        for (Extension e : extensionList) {
            if (e.getInfo().getId().equals(id)) {
                return e;
            }
        }
        extensionList.clear();
        return null;
    }

    @Override
    public synchronized void registerTask(ExtensionTask task) {
        taskMap.put(task.getId().toLowerCase(), task);
    }

    @Override
    public synchronized ExtensionTask getTaskByTaskId(String id) {
        return taskMap.get(id.toLowerCase());
    }

    @Override
    public synchronized Collection<ExtensionTask> getRegisteredTasks() {
        return Collections.unmodifiableCollection(taskMap.values());
    }

    @Override
    public synchronized void unregisterAll() {
        for (Extension extension : extensionList) {
            try {
                extension.onDisable();
                extension.setPlugin(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        extensionList.clear();
    }

    @Override
    public void unregister(Extension extension) {
        boolean remove = extensionList.remove(extension);
        if (remove) {
            extension.onDisable();
            extension.setPlugin(null);
        }
    }

    @Override
    public void shutdown() {
        unregisterAll();
        extensionList.clear();
        taskMap.clear();
    }
}
