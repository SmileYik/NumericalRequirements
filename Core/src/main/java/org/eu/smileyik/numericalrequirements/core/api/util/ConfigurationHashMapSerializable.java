package org.eu.smileyik.numericalrequirements.core.api.util;

public interface ConfigurationHashMapSerializable {
    /**
     * 序列化为 ConfigurationHashMap
     * @return
     */
    ConfigurationHashMap toConfigurationHashMap();

    /**
     * 从ConfigurationHashMap中反序列化
     * @param configurationHashMap
     */
    void fromConfigurationHashMap(final ConfigurationHashMap configurationHashMap);
}
