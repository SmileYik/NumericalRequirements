package org.eu.smileyik.numericalrequirements.core.item;

import org.eu.smileyik.numericalrequirements.core.item.tagold.service.LoreTagPattern;
import org.eu.smileyik.numericalrequirements.core.item.tagold.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

import java.util.List;

public abstract class ConsumeItemTag extends ItemTag {
    protected ConsumeItemTag(String tagId, String name, String description, LoreTagPattern pattern) {
        super(tagId, name, description, pattern);
    }

    public abstract void handlePlayer(NumericalPlayer player, LoreTagValue value);

    public void handlePlayer(NumericalPlayer player, List<LoreTagValue> valueList) {
        valueList.forEach(it -> {
            handlePlayer(player, it);;
        });
    }
}
