package org.eu.smileyik.numericalrequirements.core.api.element;

import org.eu.smileyik.numericalrequirements.core.api.AbstractRegisterInfo;

public abstract class AbstractElement extends AbstractRegisterInfo implements Element {

    public AbstractElement(String id, String name, String author, String version, String description) {
        super(id, name, author, version, description);
    }
}
