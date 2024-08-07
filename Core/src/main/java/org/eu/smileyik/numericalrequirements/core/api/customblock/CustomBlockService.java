package org.eu.smileyik.numericalrequirements.core.api.customblock;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.customblock.RealCustomBlockService;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;

import java.lang.reflect.InvocationTargetException;

/**
 * 自定义方块服务.
 */
public interface CustomBlockService {

    /**
     * 自定义方块服务依赖于CustomModelData字段, 该方法用于判断服务器是否支持该字段.
     * @return
     */
    static boolean isCompatible() {
        MySimpleReflect mySimpleReflect = null;
        try {
            mySimpleReflect = MySimpleReflect.get("org.bukkit.inventory.meta.ItemMeta#getCustomModelData()");
        } catch (Exception ignore) {

        }

        if (mySimpleReflect == null) {
            I18N.info("custom-block-is-not-compatible-your-server-version");
            return false;
        }

        return true;
    }

    /**
     * 根据配置文件构建一个 CustomBlockService 实例, 所给予的 ConfigurationSection 中,
     * 应该包含 type 字段以表明一个 CustomBlockService 实现类的全类名. 若给予的实现类不存在,
     * 则使用 RealCustomBlockService 类作为默认的类型.
     *
     * @param plugin
     * @param config
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    static CustomBlockService newInstance(NumericalRequirements plugin, ConfigurationSection config) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String classString = config.getString("type", RealCustomBlockService.class.getName());
        Class<?> aClass = Class.forName(classString);
        if (!CustomBlockService.class.isAssignableFrom(aClass)) {
            I18N.warning("wrong-custom-block-service-type", classString, RealCustomBlockService.class.getName());
            aClass = RealCustomBlockService.class;
        }

        return (CustomBlockService) aClass.getDeclaredConstructor(
                NumericalRequirements.class, ConfigurationSection.class).newInstance(plugin, config);
    }

    void initialize();

    /**
     * 关闭服务.
     */
    void shutdown();
}
