package org.eu.smileyik.numericalrequirements.core.item.tag;

public interface ItemTag <V> {
    /**
     * 获取ID，唯一标识符。建议命名应类似于以下格式
     * <p>
     *     作者名-ID
     * </p>
     * @return
     */
    String getId();

    /**
     * 获取Tag名
     * @return
     */
    String getName();

    /**
     * 获取Tag的描述。
     * @return
     */
    String getDescription();
}
