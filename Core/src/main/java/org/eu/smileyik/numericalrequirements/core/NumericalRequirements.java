package org.eu.smileyik.numericalrequirements.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.eu.smileyik.numericalrequirements.core.command.*;
import org.eu.smileyik.numericalrequirements.core.command.tabsuggests.*;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.effect.service.SimpleEffectService;
import org.eu.smileyik.numericalrequirements.core.element.service.ElementService;
import org.eu.smileyik.numericalrequirements.core.element.service.ElementServiceImpl;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionServiceImpl;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderApiExtension;
import org.eu.smileyik.numericalrequirements.core.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.item.ItemServiceImpl;
import org.eu.smileyik.numericalrequirements.core.player.service.PlayerService;
import org.eu.smileyik.numericalrequirements.core.player.service.impl.YamlPlayerService;
import org.eu.smileyik.numericalrequirements.core.util.Metrics;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NumericalRequirements extends JavaPlugin implements org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements {
    private static NumericalRequirements instance;

    private PlayerService playerService;
    private ElementService elementService;
    private EffectService effectService;
    private ExtensionService extensionService;
    private ItemService itemService;
    private PlaceholderApiExtension placeholderApiExtension;
    private CommandService commandService;
    private Metrics metrics;
    private Set<String> worlds;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            synchronized (this) {
                loadConfig();
                loadAvailableWorlds();
                setupDebugTools();
                new I18N(this, getConfig().getString("language", null));
                playerService = new YamlPlayerService(this);
                effectService = new SimpleEffectService(this);
                elementService = new ElementServiceImpl(this);
                itemService = new ItemServiceImpl(this);
                extensionService = new ExtensionServiceImpl(this);
                placeholderApiExtension = new PlaceholderApiExtension();
                extensionService.register(placeholderApiExtension);
                SimpleCommandMessageFormat simpleCommandMessageFormat = new SimpleCommandMessageFormat();
                try {
                    commandService = new CommandService(
                            simpleCommandMessageFormat,
                            simpleCommandMessageFormat,
                            RootCommand.class, ItemCommand.class, EffectCommand.class, ExtensionCommand.class
                    );
                    commandService.registerToBukkit(this);
                    commandService.registerTabSuggest(new ElementTagSuggest());
                    commandService.registerTabSuggest(new EffectIdSuggest(effectService));
                    commandService.registerTabSuggest(new PlayerNameSuggest(this));
                    commandService.registerTabSuggest(new TaskIdSuggest(extensionService));
                    commandService.registerTabSuggest(new EffectBundleSuggest());
                    commandService.registerTabSuggest(new PotionEffectSuggest());
                } catch (InvalidClassException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                extensionService.loadExtensions();
                playerService.loadOnlinePlayers();
                I18N.info("on-enable");
                if (getConfig().getBoolean("bStats", true)) {
                    metrics = new Metrics(this, 20934);
                }

                runTask(this::startTest);
            }
        });
    }

    @Override
    public void onDisable() {
        synchronized (this) {
            HandlerList.unregisterAll(this);
            playerService.shutdown();
            extensionService.shutdown();
            commandService.shutdown();
            effectService.shutdown();
            elementService.shutdown();
            itemService.shutdown();
            metrics.shutdown();
            I18N.clear();
            extensionService = null;
            commandService = null;
            effectService = null;
            elementService = null;
            itemService = null;
            playerService = null;
            metrics = null;
            try {
                DebugLogger.getInstance().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
    }

    private void loadAvailableWorlds() {
        List<String> list = getConfig().getStringList("available-worlds");
        worlds = new HashSet<>();
        for (String world : list) {
            worlds.add(world.toLowerCase());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("nradd")) {
            Player player = (Player) sender;
            ItemStack itemOnCursor = player.getItemInHand();
            ItemMeta meta = itemOnCursor.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(args[0].replace("\\_", " "));
            meta.setLore(lore);
            itemOnCursor.setItemMeta(meta);
            player.setItemInHand(itemOnCursor);
        }
        return true;
    }

    public static NumericalRequirements getInstance() {
        return instance;
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
    public void runTask(Runnable task) {
        getServer().getScheduler().runTask(this, task);
    }

    @Override
    public boolean isAvailableWorld(String worldName) {
        return worlds.contains(worldName.toLowerCase());
    }

    private void startTest() {
        try {
            Class<?> aClass = Class.forName("org.eu.smileyik.numericalrequirements.test.Test");
            aClass.getDeclaredMethod("start").invoke(null);
        } catch (Exception ignore) {

        }
    }

    private void setupDebugTools() {
        if (getConfig().getBoolean("debug", false)) {
            try {
                new DebugLogger(getLogger(), new File(getDataFolder(), "debug.log"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
