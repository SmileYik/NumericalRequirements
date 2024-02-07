package org.eu.smileyik.numericalrequirements.core.element.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.player.event.NumericalPlayerLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class ElementServiceImpl implements ElementService, Listener {
    private final List<Element> registeredElements = new ArrayList<>();
    private final NumericalRequirements plugin;

    public ElementServiceImpl(NumericalRequirements plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public synchronized void registerElement(Element element) {
        Element elementById = findElementById(element.getId());
        if (elementById != null) {
            throw new RuntimeException("Already registered a element named " + elementById.getId());
        }
        element.onRegister();
        registeredElements.add(element);
        plugin.getPlaceholderApiExtension().addPlaceholder(element);
    }

    @Override
    public synchronized void unregisterElement(Element element) {
        Element elementById = findElementById(element.getId());
        if (elementById == null) {
            return;
        }
        element.onUnregister();
        registeredElements.remove(elementById);
        plugin.getPlaceholderApiExtension().removePlaceholder(element);
    }

    @Override
    public synchronized Element findElementById(String id) {
        for (Element registeredElement : registeredElements) {
            if (id.equals(registeredElement.getId())) {
                return registeredElement;
            }
        }
        return null;
    }

    @EventHandler
    public void onLoadPlayerData(NumericalPlayerLoadEvent event) {
        synchronized (this) {
            registeredElements.forEach(element -> {
                ElementPlayer.load(event.getPlayer(), event.getSection(), element, element.newElementData());
            });
        }
    }

    @Override
    public synchronized void shutdown() {
        registeredElements.clear();
    }
}
