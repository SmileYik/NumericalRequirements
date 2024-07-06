package org.eu.smileyik.numericalrequirements.luaeffect.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;
import org.eu.smileyik.numericalrequirements.luaeffect.LuaEffectEntity;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfig;

public class LuaEffectData extends AbstractEffectData {
    private final LuaConfig luaConfig;
    private final LuaEffectEntity entity;

    private Object luaData;


    public LuaEffectData(LuaConfig luaConfig, LuaEffectEntity entity) {
        this.luaConfig = luaConfig;
        this.entity = entity;
    }

    public void setLuaData(Object luaData) {
        this.luaData = luaData;
    }

    @Override
    protected boolean doUpdate(double second) {
        super.doUpdate(second);
        if (entity.dataUpdate == null) return true;
        return luaConfig.callClosureReturnBoolean(entity.dataUpdate, luaData, second, getDuration());
    }

    @Override
    public long period() {
        return entity.period;
    }

    public Object getLuaData() {
        return luaData;
    }

    @Override
    public boolean canDelete() {
        if (entity.dataCanDelete != null) {
            return luaConfig.callClosureReturnBoolean(entity.dataCanDelete, luaData, getDuration());
        }
        return super.canDelete();
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        if (entity.dataStore != null) {
            luaConfig.callClosureReturnObject(entity.dataStore, luaData, section);
        }
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        if (entity.dataLoad != null) {
            luaConfig.callClosureReturnObject(entity.dataLoad, luaData, section);
        }
    }

    @Override
    public void setDuration(double duration) {
        super.setDuration(duration);
    }

    @Override
    public boolean isSimilar(EffectData other) {
        return other instanceof LuaEffectData &&
                ((LuaEffectData) other).entity == this.entity;
    }
}

