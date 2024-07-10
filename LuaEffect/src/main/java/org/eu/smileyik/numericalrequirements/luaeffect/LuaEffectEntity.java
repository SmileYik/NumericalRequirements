package org.eu.smileyik.numericalrequirements.luaeffect;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfig;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfigEntity;

public class LuaEffectEntity implements LuaConfigEntity {
    private final boolean outside;
    private final LuaEffectExtension extension;

    private LuaConfig config;

    public Class<Color> color = Color.class;

    /*
     * ===========================
     *         LuaEffect
     * ===========================
     */

    /*
     *
     * ----------------------------
     *           属性值
     * ----------------------------
     */
    public String id;
    public String name;
    public String author;
    public String version;
    public String description;

    /*
     * ----------------------------
     *           方法闭包.
     * ----------------------------
     */
    /**
     *  **必须赋值**
     *  类型: Lua闭包；
     *  用途：新建效果数据；
     *  形参：[LuaEffectData]；
     *  返回值：运行任意返回值返回；
     */
    public Object newEffectData;
    /**
     *  **必须赋值**
     *  类型: Lua闭包；
     *  用途：新建效果数据；
     *  形参：[LuaEffectData], [Java String 数组]；
     *  返回值：运行任意返回值返回；
     */
    public Object newEffectDataByStringArray;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：根据数据值操纵玩家数据；
     *  形参：[玩家数据：NumericalPlayer], [newEffectData返回值：Object]，[剩余时间（秒）：Double]；
     *  返回值：无返回值；
     */
    public Object handlePlayer;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：玩家首次应用此效果时触发，仅触发一次；
     *  形参：[玩家数据：NumericalPlayer], [newEffectData返回值：Object]；
     *  返回值：无返回值；
     */
    public Object onRegisterToPlayerData;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：玩家移除此效果时触发，仅触发一次；
     *  形参：[玩家数据：NumericalPlayer], [newEffectData返回值：Object]；
     *  返回值：无返回值；
     */
    public Object onUnregisterFromPlayerData;

    /*
     * ===========================
     *       LuaEffectData
     * ===========================
     */

    /*
     * ----------------------------
     *           属性值
     * ----------------------------
     */
    /**
     * 两次更新数据值之间的间隔（毫秒），
     * 即触发dataUpdate闭包时的时间间隔
     */
    public long period;

    /*
     * ----------------------------
     *           方法闭包.
     * ----------------------------
     */
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：确定此效果是否该被移除，闭包返回true时，当此效果时间被耗尽后将会被删除；
     *  形参：[newEffectData返回值：Object]，[剩余时间（秒）：Double]；
     *  返回值：boolean；
     */
    public Object dataCanDelete;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：更新数据值，返回值为true时将在下一步触发handlePlayer闭包；
     *  形参：[newEffectData返回值：Object]，[间隔时间（秒）：Double]，[剩余时间（秒）：Double]；
     *  返回值：boolean；
     */
    public Object dataUpdate;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：保存数据值；
     *  形参：[newEffectData返回值：Object]，[ConfigurationSection]；
     *  返回值：无；
     */
    public Object dataStore;
    /**
     *  **非必须赋值**
     *  类型: Lua闭包；
     *  用途：读取数据值；
     *  形参：[newEffectData返回值：Object]，[ConfigurationSection]；
     *  返回值：无；
     */
    public Object dataLoad;

    public LuaEffectEntity(boolean outside, LuaEffectExtension extension) {
        this.outside = outside;
        this.extension = extension;
    }

    public boolean isOutside() {
        return outside;
    }

    /**
     * 在同步线程中运行闭包.
     * @param obj 闭包
     */
    public void runTask(Object obj) {
        extension.getPlugin().getServer().getScheduler().runTask(extension.getPlugin(), () -> {
            try {
                config.callClosureReturnObject(obj);
            } catch (LuaException e) {
                I18N.severe("extensions.lua-effect.run-closure-failed", id, name);
                e.printStackTrace();
            }
        });
    }

    /**
     * 在同步线程中运行闭包.
     * @param obj 闭包
     * @param luaObject 参数，将传递给闭包.
     */
    public void runTask(Object obj, LuaObject luaObject) {
        extension.getPlugin().getServer().getScheduler().runTask(extension.getPlugin(), () -> {
            try {
                config.callClosureReturnObject(obj, luaObject);
            } catch (LuaException e) {
                I18N.severe("extensions.lua-effect.run-closure-failed", id, name);
                e.printStackTrace();
            }
        });
    }

    public void addPotionEffect(NumericalPlayer player, String type, int duration, int amplifier) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(type), duration, amplifier);
        extension.getPlugin().getServer().getScheduler().runTask(extension.getPlugin(), () -> {
            player.getPlayer().addPotionEffect(potionEffect);
        });
    }

    public void addPotionEffect(NumericalPlayer player, String type, int duration, int amplifier, boolean ambient, boolean particles) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(type), duration, amplifier, ambient, particles);
        extension.getPlugin().getServer().getScheduler().runTask(extension.getPlugin(), () -> {
            player.getPlayer().addPotionEffect(potionEffect);
        });
    }

    public void addPotionEffect(NumericalPlayer player, String type, int duration, int amplifier, boolean ambient, boolean particles, Color color) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(type), duration, amplifier, ambient, particles, color);
        extension.getPlugin().getServer().getScheduler().runTask(extension.getPlugin(), () -> {
            player.getPlayer().addPotionEffect(potionEffect);
        });
    }

    @Override
    public LuaConfig getLuaConfig() {
        return config;
    }

    @Override
    public void setLuaConfig(LuaConfig luaConfig) {
        this.config = luaConfig;
    }
}
