package org.eu.smileyik.numericalrequirements.luaeffect.task;

import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.core.extension.Extension;
import org.eu.smileyik.numericalrequirements.luaeffect.LuaEffectExtension;

public abstract class AbstractTask implements ExtensionTask {
    private final String id;
    private final String name;
    private final String description;
    protected final LuaEffectExtension extension;

    public AbstractTask(String id, String name, String description, LuaEffectExtension extension) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.extension = extension;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Extension getExtension() {
        return extension;
    }
}
