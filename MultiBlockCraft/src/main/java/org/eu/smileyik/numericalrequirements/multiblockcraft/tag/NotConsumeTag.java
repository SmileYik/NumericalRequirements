package org.eu.smileyik.numericalrequirements.multiblockcraft.tag;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.ItemTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagPattern;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagTypeValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

public class NotConsumeTag extends ItemTag {
    protected NotConsumeTag() {
        super(
                "mbc_no_consume",
                "不在合成时消耗",
                "不在合成时消耗",
                NumericalRequirements.getInstance().getItemService().getLoreTagService().compile("不在合成时消耗")
        );
    }

    @Override
    public boolean canMerge() {
        return false;
    }

    @Override
    public String apply(LoreTagTypeValue value, LoreTagTypeValue value2) {
        return null;
    }
}
