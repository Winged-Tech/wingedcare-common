package com.wingedtech.common.config;

import com.wingedtech.common.multitenancy.Tenant;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * 全局配置服务
 */
public class ConfigService {

    public static final String DEFAULT = "default";
    private final ConfigProvider configProvider;

    private final ConfigServiceProperties configServiceProperties;

    public ConfigService(ConfigProvider configProvider, ConfigServiceProperties configServiceProperties) {
        this.configProvider = configProvider;
        this.configServiceProperties = configServiceProperties;
    }

    /**
     * 从配置缓存中读取指定key的配置信息 - 支持多租户
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends ConfigProperties> T getConfig(Class<T> clazz) {
        return this.getTenantConfig(getConfigKey(clazz), clazz);
    }

    /**
     * 租户配置 - 从配置缓存中读取指定key的配置信息
     *
     * @param key 配置项唯一key
     * @param <T> 配置项类型
     * @return
     */
    public <T extends ConfigProperties> T getTenantConfig(String key, Class<T> clazz) {
        T objectConfig = this.getObjectConfig(getObjectIdForTenant(), key, clazz);
        if (objectConfig == null) {
            objectConfig = this.getObjectConfig(DEFAULT, key, clazz);
        }
        return objectConfig;
    }

    /**
     * 从指定的ConfigProperties类型上解析其对应的config key
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends ConfigProperties> String getConfigKey(Class<T> clazz) {
        final ConfigPropertiesKey[] annotations = clazz.getAnnotationsByType(ConfigPropertiesKey.class);
        if (ArrayUtils.isEmpty(annotations)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " doesn't have ConfigPropertiesKey annotation!");
        }
        if (annotations.length > 1) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " can only have one ConfigPropertiesKey annotation!");
        }
        return annotations[0].value();
    }

    private String getObjectIdForTenant() {
        if (Tenant.isCurrentTenantIdSet()) {
            return Tenant.getCurrentTenantId();
        } else {
            return DEFAULT;
        }
    }

    /**
     * 租户配置 - 将指定key的配置信息放入配置缓存
     *
     * @param key        配置项唯一key
     * @param properties
     * @param <T>        配置项类型
     */
    public <T extends ConfigProperties> void putTenantConfig(String key, T properties) {
        this.putObjectConfig(getObjectIdForTenant(), key, properties);
    }

    /**
     * 对象配置 - 从配置缓存中读取指定key的配置信息
     *
     * @param objectId 配置关联业务对象id
     * @param <T>      配置项类型
     * @return
     */
    public <T extends ConfigProperties> T getObjectConfig(String objectId, Class<T> clazz) {
        return this.getObjectConfig(objectId, getConfigKey(clazz), clazz);
    }

    /**
     * 对象配置 - 从配置缓存中读取指定key的配置信息
     *
     * @param key 配置项唯一key
     * @param <T>      配置项类型
     * @return
     */
    public <T extends ConfigProperties> List<T> getAllObjectConfig(String key, Class<T> clazz) {
        return configProvider.getAllConfig(key, clazz);
    }

    /**
     * 对象配置 - 从配置缓存中读取指定key的配置信息
     *
     * @param objectId 配置关联业务对象id
     * @param key      配置项唯一key
     * @param <T>      配置项类型
     * @return
     */
    public <T extends ConfigProperties> T getObjectConfig(String objectId, String key, Class<T> clazz) {
        return configProvider.getConfig(objectId, key, clazz);
    }

    /**
     * 对象配置 - 将指定key的配置信息放入配置缓存
     *
     * @param properties
     * @param <T>        配置项类型
     */
    public <T extends ConfigProperties> void putObjectConfig(String objectId, T properties) {
        configProvider.putConfig(objectId, getConfigKey(properties.getClass()), properties);
    }

    /**
     * 对象配置 - 将指定key的配置信息放入配置缓存
     *
     * @param key        配置项唯一key
     * @param properties
     * @param <T>        配置项类型
     */
    public <T extends ConfigProperties> void putObjectConfig(String objectId, String key, T properties) {
        configProvider.putConfig(objectId, key, properties);
    }
}
