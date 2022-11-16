package com.wingedtech.common.multitenancy.cache;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;

public class MultiTenantConcurrentMapCacheManager extends MultiTenantCacheManagerWrapper<ConcurrentMapCacheManager> implements BeanClassLoaderAware {
    /**
     * Specify the set of cache names for this CacheManager's 'static' mode.
     * <p>The number of caches and their names will be fixed after a call to this method,
     * with no creation of further cache regions at runtime.
     * <p>Calling this with a {@code null} collection argument resets the
     * mode to 'dynamic', allowing for further creation of caches again.
     */
    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        super.forEachTenant(cacheManager -> cacheManager.setCacheNames(cacheNames));
    }

    @Override
    protected ConcurrentMapCacheManager buildCacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.forEachTenant(cacheManager -> cacheManager.setBeanClassLoader(classLoader));
    }
}
