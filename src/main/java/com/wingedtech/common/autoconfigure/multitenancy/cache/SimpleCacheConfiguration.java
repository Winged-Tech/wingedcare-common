package com.wingedtech.common.autoconfigure.multitenancy.cache;

import com.wingedtech.common.autoconfigure.multitenancy.service.TenantInformationServiceConfiguration;
import com.wingedtech.common.multitenancy.cache.MultiTenantConcurrentMapCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Simplest cache configuration, usually used as a fallback.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(MultiTenantCacheCondition.class)
@AutoConfigureAfter(TenantInformationServiceConfiguration.class)
@Slf4j
class SimpleCacheConfiguration {

    private final CacheProperties cacheProperties;

    private final CacheManagerCustomizers customizerInvoker;

    SimpleCacheConfiguration(CacheProperties cacheProperties,
                             CacheManagerCustomizers customizerInvoker) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
    }

    @Bean
    public MultiTenantConcurrentMapCacheManager cacheManager() {
        log.info("Initializing MultiTenantConcurrentMapCacheManager");
        MultiTenantConcurrentMapCacheManager cacheManager = new MultiTenantConcurrentMapCacheManager();
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return this.customizerInvoker.customize(cacheManager);
    }

}
