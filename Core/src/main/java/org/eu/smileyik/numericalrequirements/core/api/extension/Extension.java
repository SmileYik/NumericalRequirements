package org.eu.smileyik.numericalrequirements.core.api.extension;

import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.RegisterInfo;

import java.io.File;

public abstract class Extension {
    private RegisterInfo info;
    private final NumericalRequirements plugin = (NumericalRequirements) org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements.getPlugin();
    private File dataFolder;

    public Extension() {

    }

    public Extension(RegisterInfo info) {
        this.info = info;
    }

    public final RegisterInfo getInfo() {
        return info;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements getApi() {
        return plugin;
    }

    public File getDataFolder() {
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        return dataFolder;
    }

    /**
     * 保存资源文件。
     * @param path 路径。
     * @param replace 是否替换。
     */
    public void saveResource(String path, boolean replace) {
        plugin.saveResource(String.format("%s/%s", info.getId(), path), replace);
    }

    public abstract void onEnable();
    public void onDisable() {

    }
}
