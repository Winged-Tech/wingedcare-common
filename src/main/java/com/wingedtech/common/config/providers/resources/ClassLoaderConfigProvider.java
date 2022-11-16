package com.wingedtech.common.config.providers.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wingedtech.common.config.ConfigException;
import com.wingedtech.common.config.ConfigProperties;
import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.util.ClassLoaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 基于ClassLoader，从resources目录下读取配置的ConfigProvider
 *
 * @author taozhou
 */
@Slf4j
public class ClassLoaderConfigProvider implements ConfigProvider {

    /**
     * 从配置缓存中读取指定key的配置信息
     *
     * @param key   配置项唯一key
     * @param clazz
     * @return 配置项类型集合
     */
    @Override
    public <T extends ConfigProperties> List<T> getAllConfig(String key, Class<T> clazz) {
        throw new UnsupportedOperationException("Get config is not supported on this provider!");
    }

    /**
     * 从配置缓存中读取指定key的配置信息
     * @param objectId 用于标识配置项所属对象的唯一id，该id可以是一个tenantId，或者某一个业务对象Id
     * @param key 配置项唯一key
     * @param <T> 配置项类型
     * @return
     */
    @Override
    public <T extends ConfigProperties> T getConfig(String objectId, String key, Class<T> clazz) {
        return readConfig(objectId, key, clazz);
    }


    /**
     * 将指定key的配置信息放入配置缓存
     * @param objectId 用于标识配置项所属对象的唯一id，该id可以是一个tenantId，或者某一个业务对象Id
     * @param key 配置项唯一key
     * @param properties
     * @param <T> 配置项类型
     */
    @Override
    public <T extends ConfigProperties> void putConfig(String objectId, String key, T properties) {
        throw new UnsupportedOperationException("Putting config is not supported on this provider!");
    }

    protected <T extends ConfigProperties> T readConfig(String objectId, String key, Class<T> clazz) {
        String resourcePath = String.format("config/%s-%s.json", objectId, key);
        try {
            URL configUrl = ClassLoaderUtils.findResource(resourcePath);
            if (configUrl == null) {
                throw new ConfigException("Unable to find config resource " + resourcePath);
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(configUrl, clazz);
        }
        catch(IOException ex) {
            String error = "Failed to load config resource " + resourcePath;
            log.error(error, ex);
            throw new ConfigException(error, ex);
        }
    }
}
