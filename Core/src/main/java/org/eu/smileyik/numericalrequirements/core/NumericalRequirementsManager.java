package org.eu.smileyik.numericalrequirements.core;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectService;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementService;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerService;
import org.eu.smileyik.numericalrequirements.core.command.*;
import org.eu.smileyik.numericalrequirements.core.command.tabsuggests.*;
import org.eu.smileyik.numericalrequirements.core.customblock.CustomBlockService;
import org.eu.smileyik.numericalrequirements.core.effect.SimpleEffectService;
import org.eu.smileyik.numericalrequirements.core.element.ElementServiceImpl;
import org.eu.smileyik.numericalrequirements.core.element.formatter.ElementFormatterPlaceholderCallback;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionServiceImpl;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderApiExtension;
import org.eu.smileyik.numericalrequirements.core.item.ItemServiceImpl;
import org.eu.smileyik.numericalrequirements.core.network.NetworkService;
import org.eu.smileyik.numericalrequirements.core.network.NetworkServiceImpl;
import org.eu.smileyik.numericalrequirements.core.player.YamlPlayerService;
import org.eu.smileyik.numericalrequirements.core.util.Metrics;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NumericalRequirementsManager implements NumericalRequirements {
    private final org.eu.smileyik.numericalrequirements.core.NumericalRequirements plugin;
    private final NetworkService networkService;
    private final PlayerService playerService;
    private final ElementService elementService;
    private final EffectService effectService;
    private final ExtensionService extensionService;
    private final ItemService itemService;
    private final PlaceholderApiExtension placeholderApiExtension;
    private final CommandService commandService;
    private final CustomBlockService customBlockService;

    private Metrics metrics;
    private Set<String> worlds;

    public NumericalRequirementsManager(org.eu.smileyik.numericalrequirements.core.NumericalRequirements plugin) throws InvalidClassException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        this.plugin = plugin;

        loadAvailableWorlds();
        setupDebugTools();
        new I18N(plugin, plugin.getConfig().getString("language", null));
        DataSource.init(plugin, plugin.getConfig().getConfigurationSection("datasource"));

        // services
        networkService = new NetworkServiceImpl(plugin);
        itemService = new ItemServiceImpl(plugin);
        playerService = new YamlPlayerService(plugin);
        effectService = new SimpleEffectService(plugin);
        elementService = new ElementServiceImpl(plugin);
        extensionService = new ExtensionServiceImpl(plugin);

        // setup custom block service
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("custom-block");
        CustomBlockService customBlockService = null;
        if (section != null && section.getBoolean("enable", true)) {
            if (CustomBlockService.isCompatible()) {
                customBlockService = CustomBlockService.newInstance(plugin, section);
            }
        }
        this.customBlockService = customBlockService;

        // build-in extension
        placeholderApiExtension = new PlaceholderApiExtension();

        // setup commands
        SimpleCommandMessageFormat simpleCommandMessageFormat = new SimpleCommandMessageFormat();
        commandService = new CommandService(
                simpleCommandMessageFormat,
                simpleCommandMessageFormat,
                RootCommand.class, ItemCommand.class, EffectCommand.class, ExtensionCommand.class
        );

        // setup metrics
        if (plugin.getConfig().getBoolean("bStats", true)) {
            metrics = new Metrics(plugin, 20934);
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::initialize);
    }

    private void initialize() {
        // register build-in extensions
        extensionService.register(placeholderApiExtension);
        placeholderApiExtension.addPlaceholder(new ElementFormatterPlaceholderCallback());

        // register commands
        commandService.registerToBukkit(plugin);
        commandService.registerTabSuggest(new ElementTagSuggest());
        commandService.registerTabSuggest(new EffectIdSuggest(effectService));
        commandService.registerTabSuggest(new PlayerNameSuggest(plugin));
        commandService.registerTabSuggest(new TaskIdSuggest(extensionService));
        commandService.registerTabSuggest(new EffectBundleSuggest());
        commandService.registerTabSuggest(new PotionEffectSuggest());
        commandService.registerTabSuggest(new ItemIdSuggest(plugin));

        // load custom block
        if (customBlockService != null) customBlockService.initialize();
        // load extensions
        extensionService.loadExtensions();
        // load online players
        playerService.loadOnlinePlayers();
    }

    public void shutdown() {
        networkService.shutdown();
        playerService.shutdown();
        extensionService.shutdown();
        commandService.shutdown();
        effectService.shutdown();
        elementService.shutdown();
        itemService.shutdown();
        customBlockService.shutdown();

        if (metrics != null) {
            metrics.shutdown();
        }
        metrics = null;

        worlds.clear();
        worlds = null;

        try {
            DebugLogger.getInstance().close();
        } catch (Exception ignore) {

        }
        I18N.clear();
        DataSource.close();
    }

    private void loadAvailableWorlds() {
        List<String> list = plugin.getConfig().getStringList("available-worlds");
        worlds = new HashSet<>();
        for (String world : list) {
            worlds.add(world.toLowerCase());
        }
    }

    private void setupDebugTools() {
        if (plugin.getConfig().getBoolean("debug", false)) {
            try {
                new DebugLogger(plugin.getLogger(), new File(plugin.getDataFolder(), "debug.log"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ElementService getElementService() {
        return elementService;
    }

    @Override
    public EffectService getEffectService() {
        return effectService;
    }

    @Override
    public PlayerService getPlayerService() {
        return playerService;
    }

    @Override
    public ExtensionService getExtensionService() {
        return extensionService;
    }

    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public PlaceholderApiExtension getPlaceholderApiExtension() {
        return placeholderApiExtension;
    }

    @Override
    public CommandService getCommandService() {
        return commandService;
    }

    @Override
    public NetworkService getNetworkService() {
        return networkService;
    }

    @Override
    public CustomBlockService getCustomBlockService() {
        return customBlockService;
    }

    @Override
    public void runTask(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public boolean isAvailableWorld(String worldName) {
        return worlds.contains(worldName.toLowerCase());
    }
}
