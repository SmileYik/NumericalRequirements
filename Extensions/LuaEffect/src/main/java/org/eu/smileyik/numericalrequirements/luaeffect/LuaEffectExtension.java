package org.eu.smileyik.numericalrequirements.luaeffect;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.luaeffect.effect.LuaEffect;
import org.eu.smileyik.numericalrequirements.luaeffect.task.ReloadByIdTask;
import org.eu.smileyik.numericalrequirements.luaeffect.task.ReloadTasks;
import org.keplerproject.luajava.LuaException;
import tk.smileyik.luainminecraftbukkit.LuaInMinecraftBukkit;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfig;
import tk.smileyik.luainminecraftbukkit.luaplugin.mode.inside.LuaPluginManagerInside;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LuaEffectExtension extends Extension {
    private final List<LuaEffect> luaEffectList = new ArrayList<>();

    @Override
    public void onEnable() {
        if (!getPlugin().getServer().getPluginManager().isPluginEnabled("LuaInMinecraftBukkit")) {
            I18N.info("lua-effect.lua-in-minecraft-bukkit-not-enabled");
            return;
        }

        ReloadByIdTask reloadByIdTask = new ReloadByIdTask(this);
        getApi().getExtensionService().registerTask(reloadByIdTask);
        getApi().getCommandService().registerTabSuggest(reloadByIdTask);
        ReloadTasks reloadTasks = new ReloadTasks(this);
        getApi().getExtensionService().registerTask(reloadTasks);

        discoverScripts();

        I18N.info("lua-effect.enabled");
    }

    @Override
    public void onDisable() {
        unregisterScripts();
    }

    /**
     * 发现脚本文件夹内的脚本并加载。不会对脚本是否已经加载加以判断。
     */
    public void discoverScripts() {
        File dataFolder = getDataFolder();
        File javaLuaFolder = new File(dataFolder, "inside");
        File nativeLuaFolder = new File(dataFolder, "outside");
        if (!javaLuaFolder.exists() && !javaLuaFolder.mkdirs()) {
            I18N.warning("failed-create-directors");
        }
        if (!nativeLuaFolder.exists() && !nativeLuaFolder.mkdirs()) {
            I18N.warning("failed-create-directors");
        }

        for (File file : Objects.requireNonNull(javaLuaFolder.listFiles())) {
            try {
                LuaEffectEntity entity = loadInsideScript(file);
                if (entity == null) continue;
                LuaEffect luaEffect = new LuaEffect(file, entity.getLuaConfig(), entity);
                registerScript(luaEffect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (File file : Objects.requireNonNull(nativeLuaFolder.listFiles())) {
            try {
                LuaEffectEntity entity = loadOutsideScript(file);
                if (entity == null) continue;
                LuaEffect luaEffect = new LuaEffect(file, entity.getLuaConfig(), entity);
                registerScript(luaEffect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerScript(LuaEffect luaEffect) {
        if (luaEffect == null) return;
        luaEffectList.add(luaEffect);
        getApi().getEffectService().registerEffect(luaEffect);
    }

    private void unregisterScript(String id) {
        LuaEffect luaEffect = getLuaEffectById(id);
        if (luaEffect == null) {
            return;
        }
        unregisterScript(luaEffect);
    }

    /**
     * 取消注册所有的脚本。
     */
    public void unregisterScripts() {
        for (LuaEffect luaEffect : new ArrayList<>(luaEffectList)) {
            unregisterScript(luaEffect);
        }
    }

    private void unregisterScript(LuaEffect luaEffect) {
        getApi().getEffectService().unregisterEffect(luaEffect);
        luaEffectList.remove(luaEffect);
        luaEffect.getLuaConfig().close();
    }

    public void reloadScript(String id) throws IOException, LuaException {
        LuaEffect luaEffect = getLuaEffectById(id);
        if (luaEffect == null) {
            return;
        }
        File file = luaEffect.getFile();
        unregisterScript(luaEffect);
        LuaEffectEntity luaEffectEntity;
        if (luaEffect.getEntity().isOutside()) {
            luaEffectEntity = loadOutsideScript(file);
        } else {
            luaEffectEntity = loadInsideScript(file);
        }
        assert luaEffectEntity != null;
        luaEffect = new LuaEffect(file, luaEffectEntity.getLuaConfig(), luaEffectEntity);
        registerScript(luaEffect);
    }

    public boolean isLoaded(File file) {
        for (LuaEffect luaEffect : luaEffectList) {
            if (luaEffect.getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }

    public LuaEffect getLuaEffectById(String id) {
        for (LuaEffect luaEffect : luaEffectList) {
            if (luaEffect.getId().equalsIgnoreCase(id)) {
                return luaEffect;
            }
        }
        return null;
    }

    public List<LuaEffect> getLuaEffectList() {
        return Collections.unmodifiableList(luaEffectList);
    }

    private LuaEffectEntity loadInsideScript(File file) throws LuaException, IOException {
        if (!file.getName().toLowerCase().endsWith(".lua")) return null;
        LuaEffectEntity entity = new LuaEffectEntity(true, this);
        LuaConfig.loadInsideLuaConfig(file.toPath())
                .addGlobal("config", entity)
                .addGlobal("tool", new LuaConfigTool())
                .addGlobal("logger", getPlugin().getLogger())
                .config();
        return entity;
    }

    private LuaEffectEntity loadOutsideScript(File file) throws LuaException, IOException {
        if (!file.getName().toLowerCase().endsWith(".lua")) return null;
        if (LuaInMinecraftBukkit.getPluginManager() instanceof LuaPluginManagerInside) {
            return null;
        }
        LuaEffectEntity entity = new LuaEffectEntity(true, this);
        LuaConfig.loadOutsideLuaConfig(file.toPath())
                .addGlobal("config", entity)
                .addGlobal("tool", LuaConfigTool.class)
                .addGlobal("logger", getPlugin().getLogger())
                .config();
        return entity;
    }
}
