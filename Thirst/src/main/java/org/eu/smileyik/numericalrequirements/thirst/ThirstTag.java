package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.item.ConsumeItemTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagTypeValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

public class ThirstTag extends ConsumeItemTag {
    private final ThirstElement element;
    private final ElementHandler elementHandler;
    protected ThirstTag(ThirstElement element, ConfigurationSection config, ElementHandler elementHandler) {
        super(
                "Thirst",
                I18N.tr("extension.thirst.tag.name"),
                I18N.tr("extension.thirst.tag.description"),
                NumericalRequirements.getInstance().getItemService().getLoreTagService().compile(
                        config.getString("tag.thirst", "§b增加 §2<%:numf1> §b滋润度.")
                )
        );
        this.element = element;
        this.elementHandler = elementHandler;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, LoreTagValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, element);
        assert elementData != null;
        LoreTagTypeValue loreTagTypeValue = value.get(0);
        double v = ((Number) loreTagTypeValue.getValue()).doubleValue();
        ThirstData data = (ThirstData) elementData;
        data.calculateAndGet(v, Double::sum);
        elementHandler.handlePlayer(player, data);
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public String apply(LoreTagTypeValue p, LoreTagTypeValue q) {
        double v = ((Number) p.getValue()).doubleValue() + ((Number) q.getValue()).doubleValue();
        return String.valueOf(v);
    }
}
