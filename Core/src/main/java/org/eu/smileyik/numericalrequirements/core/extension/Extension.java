package org.eu.smileyik.numericalrequirements.core.extension;

import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.RegisterInfo;

import java.io.File;

public abstract class Extension {
    private RegisterInfo info;
    private NumericalRequirements plugin;
    private File dataFolder;

    public Extension() {

    }

    public Extension(RegisterInfo info) {
        this.info = info;
    }

    public final RegisterInfo getInfo() {
        return info;
    }
    protected synchronized final void setPlugin(Plugin plugin) {
        if (this.plugin == null) {
            this.plugin = (NumericalRequirements) plugin;
            this.dataFolder = new File(this.plugin.getDataFolder(), info.getId());
            if (this.dataFolder.exists()) {
                this.dataFolder.mkdirs();
            }
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements getApi() {
        return plugin;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    protected abstract void onEnable();
    protected void onDisable() {

    }
}
