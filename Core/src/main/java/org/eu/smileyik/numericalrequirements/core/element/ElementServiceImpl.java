package org.eu.smileyik.numericalrequirements.core.element;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.element.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementService;
import org.eu.smileyik.numericalrequirements.core.api.event.player.NumericalPlayerLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class ElementServiceImpl implements ElementService, Listener {
    private final List<Element> registeredElements = new ArrayList<>();
    private final NumericalRequirements plugin;

    public ElementServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        if (config.isConfigurationSection("formatter")) {
            ConfigurationSection section = config.getConfigurationSection("formatter");
            section.getKeys(false).forEach(key -> {
                ElementFormatter<?, ?> elementFormatter = ElementFormatter.ELEMENT_FORMATTERS.get(key);
                if (elementFormatter != null) {
                    elementFormatter.configure(section.getConfigurationSection(key));
                }
            });
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void registerElement(Element element) {
        Element elementById = findElementById(element.getId());
        if (elementById != null) {
            throw new RuntimeException("Already registered a element named " + elementById.getId());
        }
        element.onRegister();
        registeredElements.add(element);
        plugin.getPlaceholderApiExtension().addPlaceholder(element);
    }

    @Override
    public void unregisterElement(Element element) {
        Element elementById = findElementById(element.getId());
        if (elementById == null) {
            return;
        }
        element.onUnregister();
        registeredElements.remove(elementById);
        plugin.getPlaceholderApiExtension().removePlaceholder(element);
    }

    @Override
    public Element findElementById(String id) {
        for (Element registeredElement : registeredElements) {
            if (id.equals(registeredElement.getId())) {
                return registeredElement;
            }
        }
        return null;
    }

    @EventHandler
    public void onLoadPlayerData(NumericalPlayerLoadEvent event) {
        {
            registeredElements.forEach(element -> {
                ElementPlayer.load(event.getPlayer(), event.getSection(), element, element.newElementData());
            });
        }
    }

    @Override
    public void shutdown() {
        registeredElements.clear();
    }
}
