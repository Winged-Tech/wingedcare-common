package com.wingedtech.common.config.providers.properties;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.wingedtech.common.config.ConfigProperties;
import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 基于externalized properties的ConfigProvider
 */
@Slf4j
public class PropertiesConfigProvider implements ConfigProvider {

    private static final String PROPERTIES_ROOT = Constants.CONFIG_SERVICE_PROPERTIES_ROOT + ".data";

    private final Binder binder;

    private Cache<String, Optional<ConfigProperties>> cache = CacheBuilder.newBuilder().maximumSize(20).build();

    public PropertiesConfigProvider(Binder binder) {
        this.binder = binder;
    }

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
        Bindable<T> bindable = Bindable.of(clazz);
        String name = getPropertiesPath(objectId, key);
        try {
            final Optional<ConfigProperties> properties = cache.get(name, () -> {
                final BindResult<T> bindResult = binder.bind(name, bindable);
                if (bindResult.isBound()) {
                    return Optional.ofNullable(bindResult.get());
                }
                return Optional.empty();
            });
            return (T) properties.orElse(null);
        } catch (ExecutionException e) {
            log.error("get config with cache execution exception:", e);
            return null;
        }
    }

    private String getPropertiesPath(String objectId, String key) {
        StringBuilder builder = new StringBuilder();
        // Spring property 绑定时, 只允许小写格式的名字, 这里防止tenantId出现大写字母
        // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-relaxed-binding
        builder.append(PROPERTIES_ROOT).append(".").append(StringUtils.lowerCase(objectId)).append(".").append(key);
        return builder.toString();
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
}
