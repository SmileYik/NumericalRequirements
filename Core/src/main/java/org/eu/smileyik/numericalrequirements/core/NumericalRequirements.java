package org.eu.smileyik.numericalrequirements.core;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NumericalRequirements extends JavaPlugin {
    private static NumericalRequirements instance;

    private NumericalRequirementsManager manager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        final Object that = this;
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            synchronized (that) {
                loadConfig();

                try {
                    manager = new NumericalRequirementsManager(this);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

                I18N.info("on-enable");
                manager.runTask(this::startTest);
            }
        });
    }

    @Override
    public void onDisable() {
        synchronized (this) {
            stopTest();
            HandlerList.unregisterAll(this);

            if (manager != null) {
                manager.shutdown();
                manager = null;
            }
        }
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
    }

    public static NumericalRequirements getInstance() {
        return instance;
    }

    public NumericalRequirementsManager getManager() {
        return manager;
    }

    /**
     * 运行测试.
     */
    private void startTest() {
        try {
            if (!getConfig().getBoolean("test", false)) {
                return;
            }
            Class<?> aClass = Class.forName("org.eu.smileyik.numericalrequirements.test.Test");
            aClass.getDeclaredMethod("start").invoke(null);
        } catch (Exception ignore) {

        }
    }

    /**
     * 停止测试.
     */
    private void stopTest() {
        try {
            if (!getConfig().getBoolean("test", false)) {
                return;
            }
            Class<?> aClass = Class.forName("org.eu.smileyik.numericalrequirements.test.Test");
            aClass.getDeclaredMethod("stop").invoke(null);
        } catch (Exception ignore) {

        }
    }
}
