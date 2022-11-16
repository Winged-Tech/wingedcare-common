package com.wingedtech.common.cache.config;

import com.wingedtech.common.cache.guava.GuavaCacheManager;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.wingedtech.common.cache.constant.Constants.GUAVA_CACHE_CONFIG_ROOT;

/**
 * GuavaCacheManage 相关配置
 *
 * @author zhangyp
 */
@Data
@Component
@ConfigurationProperties(GUAVA_CACHE_CONFIG_ROOT)
public class GuavaCacheManageProperties {

    /**
     * 是否自动开启GuavaCache，默认关闭
     */
    private boolean enabled = false;

    /**
     * GuavaCacheManager创建GuavaCache时的特殊配置
     */
    private String spec;

    /**
     * GuavaCache相关配置
     */
    private List<GuavaCacheProperties> data;

    public GuavaCacheManager buildGuavaCacheManager() {
        GuavaCacheManager manager = new GuavaCacheManager();
        manager.setCaches(data.stream().map(guavaCacheProperties -> guavaCacheProperties.buildGuavaCache()).collect(Collectors.toList()));
        manager.setSpec(spec);
        manager.afterPropertiesSet();
        return manager;
    }
}
