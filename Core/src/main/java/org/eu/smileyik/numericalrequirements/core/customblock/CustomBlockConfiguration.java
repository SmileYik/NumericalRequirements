package org.eu.smileyik.numericalrequirements.core.customblock;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.eu.smileyik.numericalrequirements.core.api.customblock.CustomBlock;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

/**
 * 数据库实体类, 可以根据下列字段构建 CustomBlock.
 */
@DatabaseTable(tableName = "nreq_custom_block")
public class CustomBlockConfiguration {
    /**
     * 自定义方块的ID
     */
    @DatabaseField(id = true)
    private String id;

    /**
     * 自定义方块的类型
     */
    @DatabaseField
    private String type;

    /**
     * 自定义方块所使用的物品ID
     */
    @DatabaseField(unique = true, columnName = "item-id")
    private String itemId;

    /**
     * 自定义方块的其他配置项.
     */
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String config;

    public CustomBlockConfiguration() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    /**
     * 根据现有的配置构建一个自定义方块接口的实例.
     * @return
     */
    public CustomBlock toCustomBlock() {
        try {
            ConfigurationHashMap map = ConfigurationHashMap.fromJson(config == null ? "{}" : config);
            map.put("type", type);
            map.put("block-item-id", itemId);
            map.put("id", id);
            return CustomBlock.loadFromConfigurationHashMap(map);
        } catch (Throwable e) {
            DebugLogger.debug(e);
        }
        return null;
    }
}
