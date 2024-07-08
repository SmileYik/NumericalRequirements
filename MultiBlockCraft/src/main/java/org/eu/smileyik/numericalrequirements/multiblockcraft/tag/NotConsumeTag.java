package org.eu.smileyik.numericalrequirements.multiblockcraft.tag;

import org.eu.smileyik.numericalrequirements.core.item.tag.lore.LoreTag;

public class NotConsumeTag extends LoreTag {

    @Override
    protected String getModeString() {
        return "不在合成时消耗";
    }

    @Override
    public String getId() {
        return "mbc_no_consume";
    }

    @Override
    public String getName() {
        return "不在合成时消耗";
    }

    @Override
    public String getDescription() {
        return "不在合成时消耗";
    }
}
