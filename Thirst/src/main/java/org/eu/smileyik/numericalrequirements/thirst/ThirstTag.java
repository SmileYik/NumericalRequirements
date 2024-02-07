package org.eu.smileyik.numericalrequirements.thirst;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.item.ConsumeItemTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagTypeValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

import java.util.List;

public class ThirstTag extends ConsumeItemTag {
    protected ThirstTag() {
        super("Thirst", "口渴度标签", "使用带有此标签的物品将会对口渴值产生影响", NumericalRequirements.getInstance().getItemService().getLoreTagService().compile("§b增加 §2<%:numf1> §b滋润度."));
    }

    @Override
    public void handlePlayer(NumericalPlayer player, LoreTagValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, ThirstExtension.element);
        LoreTagTypeValue loreTagTypeValue = value.get(0);
        double v = ((Number) loreTagTypeValue.getValue()).doubleValue();
        ThirstData data = (ThirstData) elementData;
        data.calculateAndGet(v, Double::sum);
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
