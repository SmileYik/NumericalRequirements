package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;

public class MachineLoreTag extends LoreTag {
    @Override
    protected String getModeString() {
        return "机器: <%:str>";
    }

    @Override
    public String getId() {
        return "machine-lore";
    }

    @Override
    public String getName() {
        return "机器类型";
    }

    @Override
    public String getDescription() {
        return "机器类型";
    }
}
