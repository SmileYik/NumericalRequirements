package org.eu.smileyik.numericalrequirements.core.effect;

import org.eu.smileyik.numericalrequirements.core.AbstractRegisterInfo;

public abstract class AbstractEffect extends AbstractRegisterInfo implements Effect {
    public AbstractEffect(String id, String name, String author, String version, String description) {
        super(id, name, author, version, description);
    }
}
