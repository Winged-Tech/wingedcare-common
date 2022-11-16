package com.wingedtech.common.multitenancy.cache;

import org.springframework.cache.support.NoOpCacheManager;

public class MultiTenantNoOpCacheManager extends MultiTenantCacheManagerWrapper<NoOpCacheManager> {
    @Override
    protected NoOpCacheManager buildCacheManager() {
        return new NoOpCacheManager();
    }
}
