package com.wingedtech.common.autoconfigure.multitenancy.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * No-op cache configuration used to disable caching via configuration.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@Conditional(MultiTenantCacheCondition.class)
class NoOpCacheConfiguration {

    @Bean
    public NoOpCacheManager cacheManager() {
        return new NoOpCacheManager();
    }

}

