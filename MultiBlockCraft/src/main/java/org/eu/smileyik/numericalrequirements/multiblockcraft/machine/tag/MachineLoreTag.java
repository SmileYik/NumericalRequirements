package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;

public class MachineLoreTag extends LoreTag {
    @Override
    protected String getModeString() {
        return MultiBlockCraftExtension.getConfig().getString("machine.tag.lore-machine", "机器: <%:str>");
    }

    @Override
    public String getId() {
        return "lore-machine";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.machine.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.machine.description");
    }
}
