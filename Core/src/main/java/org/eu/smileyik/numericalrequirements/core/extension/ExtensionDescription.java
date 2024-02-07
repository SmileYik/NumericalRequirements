package org.eu.smileyik.numericalrequirements.core.extension;

import org.eu.smileyik.numericalrequirements.core.AbstractRegisterInfo;

public class ExtensionDescription extends AbstractRegisterInfo {
    public ExtensionDescription(String id, String name, String author, String version, String description) {
        super(id, name, author, version, description);
    }

    @Override
    @Deprecated
    protected void register() {

    }

    @Override
    @Deprecated
    protected void unregister() {

    }
}
