package org.eu.smileyik.numericalrequirements.core.element;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.AbstractRegisterInfo;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;

public abstract class AbstractElement extends AbstractRegisterInfo implements Element {

    public AbstractElement(String id, String name, String author, String version, String description) {
        super(id, name, author, version, description);
    }
}
