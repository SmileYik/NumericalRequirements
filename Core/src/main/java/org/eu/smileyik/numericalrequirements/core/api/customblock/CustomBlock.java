package org.eu.smileyik.numericalrequirements.core.api.customblock;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;

import java.lang.reflect.InvocationTargetException;

public interface CustomBlock {

    /**
     * 获取该自定义方块的ID
     * @return
     */
    String getId();

    /**
     * 获取方块物品的ID, 该物品将作为掉落物品提供, 并作为放置自定义方块的凭据.
     * @return
     */
    String getBlockItemId();

    /**
     * 设置该自定义方块的ID
     * @param id
     */
    void setId(String id);

    default void rightClick(Player player, Location clickedLocation, BlockFace clickedBlockFace) {

    }

    default void leftClick(Player player, Location clickedLocation, BlockFace clickedBlockFace) {

    }

    /**
     * 放置方块
     * @param pos
     */
    void place(Pos pos);

    /**
     * 移除方块
     * @param pos
     */
    void remove(Pos pos);

    void remove(Pos pos, boolean isUnload);

    /**
     * 掉落掉落物品.
     * @param player
     * @param location
     */
    void drop(Player player, Location location);

    /**
     * 从配置文件中加载.
     * @param section
     */
    default void load(ConfigurationHashMap section) {
        setId(section.getString("id"));
    }

    /**
     * 保存至配置文件
     * @param section
     */
    default void save(ConfigurationHashMap section) {
        section.put("type", getClass().getName());
        section.put("id", getId());
    }

    /**
     * 从配置文件中读取一个有效的自定义方块实例.
     * 所提供的 ConfigurationSection 中应该包含一个 type 字段,
     * 用来指明此接口的一个实现类的全类名.
     * @param section
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    static CustomBlock loadFromConfigurationSection(ConfigurationSection section) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return loadFromConfigurationHashMap(YamlUtil.toMap(section));
    }

    static CustomBlock loadFromConfigurationHashMap(ConfigurationHashMap map) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String string = map.getString("type");
        if (string == null) return null;
        CustomBlock newInstance = (CustomBlock) Class.forName(string).getDeclaredConstructor().newInstance();
        newInstance.load(map);
        return newInstance;
    }
}
