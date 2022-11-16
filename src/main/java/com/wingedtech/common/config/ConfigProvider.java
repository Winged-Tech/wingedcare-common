package com.wingedtech.common.config;

import java.util.List;

/**
 * 所有配置provider的基本接口
 */
public interface ConfigProvider {

    /**
     * 从配置缓存中读取指定key的配置信息
     *
     * @param key 配置项唯一key
     * @param <T> 配置项类型
     * @return 配置项类型集合
     */
    <T extends ConfigProperties> List<T> getAllConfig(String key, Class<T> clazz);

    /**
     * 从配置缓存中读取指定key的配置信息
     *
     * @param objectId 用于标识配置项所属对象的唯一id，该id可以是一个tenantId，或者某一个业务对象Id
     * @param key      配置项唯一key
     * @param <T>      配置项类型
     * @return 配置项类型
     */
    <T extends ConfigProperties> T getConfig(String objectId, String key, Class<T> clazz);

    /**
     * 将指定key的配置信息放入配置缓存
     *
     * @param objectId 用于标识配置项所属对象的唯一id，该id可以是一个tenantId，或者某一个业务对象Id
     * @param key      配置项唯一key
     * @param <T>      配置项类型
     */
    <T extends ConfigProperties> void putConfig(String objectId, String key, T properties);
}
