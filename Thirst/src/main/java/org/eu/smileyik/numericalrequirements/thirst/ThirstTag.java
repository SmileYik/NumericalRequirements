package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ConsumableTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.MergeableLore;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

public class ThirstTag extends LoreTag implements ConsumableTag<LoreValue>, MergeableLore {

    private final ConfigurationSection config;
    private final ThirstElement element;
    private final ElementHandler elementHandler;

    protected ThirstTag(ThirstElement element, ConfigurationSection config, ElementHandler elementHandler) {
        super(config.getString("tag.thirst", "§b增加 §2<%:numf1> §b滋润度."));
        this.element = element;
        this.elementHandler = elementHandler;
        this.config = config;
    }

    @Override
    public void onConsume(NumericalPlayer player, LoreValue value) {
        Element elementData = ElementPlayer.getElementData(player, element);
        assert elementData != null;
        double v = ((Number) value.get(0)).doubleValue();
        ThirstData data = (ThirstData) elementData;
        data.calculateAndGet(v, Double::sum);
        elementHandler.handlePlayer(player, data);
    }

    @Override
    protected String getModeString() {
        return config.getString("tag.thirst", "§b增加 §2<%:numf1> §b滋润度.");
    }

    @Override
    public String getId() {
        return "Thirst";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.thirst.tag.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.thirst.tag.description");
    }
}
