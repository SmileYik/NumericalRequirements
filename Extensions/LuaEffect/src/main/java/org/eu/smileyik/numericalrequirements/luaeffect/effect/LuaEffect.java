package org.eu.smileyik.numericalrequirements.luaeffect.effect;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;
import org.eu.smileyik.numericalrequirements.luaeffect.LuaEffectEntity;
import org.keplerproject.luajava.LuaException;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfig;

import java.io.File;

public class LuaEffect extends AbstractEffect {
    private final File file;
    private final LuaConfig luaConfig;
    private final LuaEffectEntity entity;
    public LuaEffect(File file, LuaConfig luaConfig, LuaEffectEntity entity) {
        super(
                entity.id,
                entity.name,
                entity.author,
                entity.version,
                entity.description
        );
        this.file = file;
        this.luaConfig = luaConfig;
        this.entity = entity;
    }

    @Override
    protected void register() {

    }

    @Override
    protected void unregister() {

    }

    @Override
    public EffectData newEffectData() {
        LuaEffectData luaEffectData = new LuaEffectData(luaConfig, entity);
        Object luaObject = null;
        try {
            luaObject = luaConfig.callClosureReturnObject(entity.newEffectData, luaEffectData);
        } catch (LuaException e) {
            I18N.severe("extensions.lua-effect.run-new-effect-failed", entity.id, entity.name);
            e.printStackTrace();
        }
        luaEffectData.setLuaData(luaObject);
        return luaEffectData;
    }

    @Override
    public EffectData newEffectData(String[] args) {
        LuaEffectData luaEffectData = new LuaEffectData(luaConfig, entity);
        Object luaObject = null;
        try {
            luaObject = luaConfig.callClosureReturnObject(entity.newEffectDataByStringArray, luaEffectData, (Object) args);
        } catch (LuaException e) {
            I18N.severe("extensions.lua-effect.run-new-effect-by-string-array-failed", entity.id, entity.name);
            e.printStackTrace();
        }
        luaEffectData.setLuaData(luaObject);
        return luaEffectData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerValue value) {
        if (entity.handlePlayer == null) return;
        LuaEffectData data = (LuaEffectData) value;
        try {
            luaConfig.callClosureReturnObject(
                    entity.handlePlayer,
                    player, data.getLuaData(), data.getDuration());
        } catch (LuaException e) {
            I18N.severe("extensions.lua-effect.run-method-handle-player-failed", entity.id, entity.name);
            e.printStackTrace();
        }
    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerValue value) {
        if (entity.onRegisterToPlayerData == null) return;
        Object luaData = ((LuaEffectData) value).getLuaData();
        try {
            luaConfig.callClosureReturnObject(entity.onRegisterToPlayerData, player, luaData);
        } catch (LuaException e) {
            I18N.severe("extensions.lua-effect.register-player-data-failed", entity.id, entity.name);
            e.printStackTrace();
        }
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerValue value) {
        if (entity.onUnregisterFromPlayerData == null) return;
        Object luaData = ((LuaEffectData) value).getLuaData();
        try {
            luaConfig.callClosureReturnObject(entity.onUnregisterFromPlayerData, player, luaData);
        } catch (LuaException e) {
            I18N.severe("extensions.lua-effect.unregister-player-data-failed", entity.id, entity.name);
            e.printStackTrace();
        }
    }

    public LuaConfig getLuaConfig() {
        return luaConfig;
    }

    public File getFile() {
        return file;
    }

    public LuaEffectEntity getEntity() {
        return entity;
    }
}
