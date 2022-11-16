package com.wingedtech.common.multitenancy.cache;

import org.springframework.cache.support.SimpleCacheManager;

/**
 * 多租户版本的SimpleCacheManager
 */
public class MultiTenantSimpleCacheManager extends MultiTenantCacheManagerWrapper<SimpleCacheManager> {
    @Override
    protected SimpleCacheManager buildCacheManager() {
        return new SimpleCacheManager();
    }
}
