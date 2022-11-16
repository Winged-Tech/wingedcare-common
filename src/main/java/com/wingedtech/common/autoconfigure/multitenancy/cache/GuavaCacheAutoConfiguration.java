package com.wingedtech.common.autoconfigure.multitenancy.cache;

import com.wingedtech.common.cache.config.GuavaCacheManageProperties;
import com.wingedtech.common.cache.guava.GuavaCacheManager;
import com.wingedtech.common.multitenancy.ConditionalOnMultiTenant;
import com.wingedtech.common.multitenancy.cache.MultiTenantGuavaCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.cache.constant.Constants.GUAVA_CACHE_CONFIG_ROOT;
import static com.wingedtech.common.constant.ConfigConstants.ENABLED;


/**
 * GuavaCacheManager 配置项
 *
 * @author zhangyp
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({GuavaCacheManageProperties.class})
@ConditionalOnProperty(value = ENABLED, prefix = GUAVA_CACHE_CONFIG_ROOT)
@EnableCaching
public class GuavaCacheAutoConfiguration {

    public static final String CACHE_MANAGER = "cacheManager";

    final private GuavaCacheManageProperties guavaCacheManageProperties;

    public GuavaCacheAutoConfiguration(GuavaCacheManageProperties guavaCacheManageProperties) {
        this.guavaCacheManageProperties = guavaCacheManageProperties;
    }

    @Bean(name = CACHE_MANAGER)
    @ConditionalOnMultiTenant
    public MultiTenantGuavaCacheManager multiTenantCacheManager() {
        return new MultiTenantGuavaCacheManager(guavaCacheManageProperties);
    }

    @Bean(name = CACHE_MANAGER)
    @ConditionalOnMissingBean(name = CACHE_MANAGER)
    public GuavaCacheManager guavaCacheManager() {
        return guavaCacheManageProperties.buildGuavaCacheManager();
    }

}
